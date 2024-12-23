package com.example.idiotchefassistant.recipe

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.fragment.app.DialogFragment
import com.example.idiotchefassistant.databinding.FragmentPostCommentBinding
import com.example.idiotchefassistant.recipeAddTokenService
import com.example.idiotchefassistant.recipeService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostCommentPage : DialogFragment() {
    private var _binding: FragmentPostCommentBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostCommentBinding.inflate(inflater, container, false)
        val view = binding.root
        val rating = binding.reviewStar
        val comment = binding.commentBlock
        val close = binding.closeButton
        val upload = binding.uploadButton
        val rid = arguments?.getInt("rid", 0) ?: 0

        rating.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { ratingBar, star, fromUser ->
            if (fromUser) {
                val roundedRate = Math.round(star)
                ratingBar.rating = roundedRate.toFloat()
            }
        }

        close.setOnClickListener{
            dismiss()
        }

        upload.setOnClickListener{
            post(rid, comment.text.toString(), rating.rating.toInt())
            dismiss()
        }

        return view
    }

    private fun post(rid: Int, content: String, rate: Int){
        Log.i("postComment","Content:$rid $content $rate")
        val body = CommentBody(rid, content, rate)
        recipeAddTokenService.postComment(body).enqueue(object :
            Callback<MessageBody> {
            override fun onResponse(
                call: Call<MessageBody>,
                response: Response<MessageBody>
            ) {
                if (response.isSuccessful) {
                    Log.i("postComment","Success:${response.body().toString()}")
                } else {
                    Log.i("postComment","Failed:${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<MessageBody>, t: Throwable) {
                Log.e("postComment", "API call failed: ${t.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

