package `in`.minewave.janusvideoroom

import `in`.minewave.janusvideoroom.databinding.ActivityIntroBinding
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class IntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.connectButton.setOnClickListener {
            var roomId = binding.roomEdittext.text
            if(roomId.length>2) {
                Intent(this@IntroActivity, MainActivity::class.java).apply {
                    putExtra("roomId", roomId.toString())
                }.also {
                    startActivity(it)
                }
            }
        }
    }


}