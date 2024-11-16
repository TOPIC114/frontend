package com.example.idiotchefassistant.ingredientsBlock

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.example.idiotchefassistant.databinding.FragmentIngredientDialogBinding
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider

class IngredientDialogFragment : DialogFragment() {
    private var _binding: FragmentIngredientDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var ingredientViewModel: IngredientViewModel
    private lateinit var ingredientFactory: IngredientFactory
    private lateinit var ingredientRepository: IngredientRepository
    private var selectedPosition: Int? = null
    private var selectedItemName: String? = null
    private var listener: OnItemSelectedListener? = null
    private var isSeason = false

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIngredientDialogBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.listViewItems
        ingredientRepository = IngredientRepository()
        ingredientFactory = IngredientFactory(ingredientRepository)
        ingredientViewModel = ViewModelProvider(this, ingredientFactory)[IngredientViewModel::class.java]

        // Set up the list and adapter
        ingredientViewModel.getData()
        val adapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                if (position == selectedPosition) {
                    v.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.holo_orange_light))
                } else {
                    v.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
                }
                return v
            }
        }

        binding.listViewItems.adapter = adapter
        ingredientViewModel.callBack().observe(viewLifecycleOwner) { ingredientData ->
            val items = ingredientData.ingredientNames ?: emptyArray()
            adapter.clear()
            adapter.addAll(items.toList())
            adapter.notifyDataSetChanged()
        }

        binding.buttonSwitch.setOnClickListener{
            isSeason = !isSeason
            if(isSeason)
                binding.buttonSwitch.text = "尋找食材"
            else
                binding.buttonSwitch.text = "尋找調味料"
            ingredientViewModel.switchData(isSeason)
        }

        binding.listViewItems.setOnItemClickListener { _, _, position, _ ->
            selectedPosition = position
            selectedItemName = adapter.getItem(position)
            adapter.notifyDataSetChanged()
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.notifyDataSetChanged()
                val query = s.toString()
                ingredientViewModel.filterItems(query)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.buttonCancel.setOnClickListener { dismiss() }
        binding.buttonConfirm.setOnClickListener {
            listener?.onItemSelected(selectedItemName)
            dismiss()
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setOnItemSelectedListener(listener: OnItemSelectedListener) {
        this.listener = listener
    }

    interface OnItemSelectedListener {
        fun onItemSelected(itemName: String?)
    }
}

