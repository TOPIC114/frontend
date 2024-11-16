package com.example.idiotchefassistant.resultBlock

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.idiotchefassistant.ingredientsBlock.IngredientDialogFragment
import com.example.idiotchefassistant.searchBlock.SearchPage
import com.example.idiotchefassistant.databinding.ActivityResultPageBinding
import com.example.idiotchefassistant.ingredientsBlock.IngredientFactory
import com.example.idiotchefassistant.ingredientsBlock.IngredientRepository
import com.example.idiotchefassistant.ingredientsBlock.IngredientViewModel

class ResultPage : AppCompatActivity(), ResultItemAdapter.OnItemClickListener, IngredientDialogFragment.OnItemSelectedListener {
    private lateinit var binding: ActivityResultPageBinding
    private lateinit var resultViewModel: ResultViewModel
    private lateinit var resultFactory: ResultFactory
    private lateinit var resultRepository: ResultRepository
    private lateinit var ingredientViewModel:IngredientViewModel
    private lateinit var ingredientFactory: IngredientFactory
    private lateinit var ingredientRepository: IngredientRepository
    private lateinit var progressDialog: AlertDialog

    private lateinit var adapter: ResultItemAdapter
    private var isEditMode = false
    private var editItemTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityResultPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        resultRepository = ResultRepository()
        resultFactory = ResultFactory(resultRepository)
        resultViewModel = ViewModelProvider(this, resultFactory)[ResultViewModel::class.java]
        ingredientRepository = IngredientRepository()
        ingredientFactory = IngredientFactory(ingredientRepository)
        ingredientViewModel = ViewModelProvider(this, ingredientFactory)[IngredientViewModel::class.java]

        val photos = intent.getStringArrayListExtra("photoFilePaths")
        try {
            resultViewModel.uploadPhotos(photos)
        }catch (e:Exception){
            Log.e("NetworkError", "Unable to resolve host: ${e.message}")
        }
        // get the data from server
        val recyclerView = binding.recyclerViewIngredients
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ResultItemAdapter(emptyList())
        adapter.setOnItemClickListener(this)
        recyclerView.adapter = adapter
        resultViewModel.callBack().observe(this) {
            val items = it.result?.map { entry ->
                ResultItem(entry.value.images, entry.value.name)
            } ?: emptyList()
            adapter.updateItems(items)
        }

        resultViewModel.isUploading.observe(this) { isUploading ->
            if (isUploading) {
                showProgressDialog()
            } else {
                hideProgressDialog()
            }
        }
        resultViewModel.uploadResult.observe(this) { isSuccess ->
            if (!isSuccess) {
                AlertDialog.Builder(this)
                    .setTitle("Upload Failed")
                    .setMessage("The pictures upload failed, please try again.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }

        binding.addButton.setOnClickListener {
            val dialog = IngredientDialogFragment()
            dialog.setOnItemSelectedListener(this)
            isEditMode = false
            dialog.show(supportFragmentManager, "customDialog")
        }

        binding.searchButton.setOnClickListener {
            val iids = ArrayList(resultRepository.getData()?.result?.keys?: emptyList())
            resultViewModel.resultSearch().observe(this){ rList->
                val intent = Intent(this, SearchPage::class.java).apply {
                    putParcelableArrayListExtra("rItems", ArrayList(rList))
                    putIntegerArrayListExtra("iids", iids)
                }
                startActivity(intent)
            }
        }
    }

    override fun onEditClick(item: ResultItem) {
        val dialog = IngredientDialogFragment()
        dialog.setOnItemSelectedListener(this)
        isEditMode = true
        editItemTitle = item.name
        dialog.show(supportFragmentManager, "customDialog")
    }

    override fun onItemSelected(itemName: String?) {
        if (isEditMode) {
            if (resultViewModel.findData(itemName.toString())) {
                AlertDialog.Builder(this)
                    .setTitle("Item Exist Already")
                    .setMessage("The item \"${itemName}\" does exist in the list.")
                    .setPositiveButton("OK", null)
                    .show()
            } else {
                resultViewModel.editData(editItemTitle!!, itemName.toString())
            }
        } else {
            if (resultViewModel.findData(itemName.toString())) {
                AlertDialog.Builder(this)
                    .setTitle("Item Exist Already")
                    .setMessage("The item \"${itemName}\" does exist in the list.")
                    .setPositiveButton("OK", null)
                    .show()
            } else {
                resultViewModel.addData(
                    itemName.toString(),
                    arrayListOf()
                ) // Pass the image path here
            }
        }
        adapter.updateItems(resultRepository.getData()?.result?.map { ResultItem(it.value.images, it.value.name) } ?: emptyList())
    }

    override fun onDeleteClick(item: ResultItem) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this item?")
            .setPositiveButton("Yes") { _, _ ->
                if(!resultViewModel.findData(item.name)){
                    AlertDialog.Builder(this)
                    .setTitle("Item Not Found")
                    .setMessage("The item \"${item.name}\" does not exist in the list.")
                    .setPositiveButton("OK", null)
                    .show()
                }
                else {
                    resultViewModel.deleteData(item.name)
                }
                adapter.updateItems(resultRepository.getData()?.result?.map { ResultItem(it.value.images, it.value.name) } ?: emptyList())

            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun showProgressDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Uploading Pictures...")

        val progressBar = ProgressBar(this)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        progressBar.layoutParams = params

        builder.setView(progressBar)
        builder.setCancelable(false) // 阻止取消 dialog

        progressDialog = builder.create()
        progressDialog.show()
    }

    private fun hideProgressDialog() {
        if (this::progressDialog.isInitialized && progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

}

