package com.example.book

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.book.databinding.MainScreenBinding

class MainScreenActivity : AppCompatActivity() {

    private val viewModel: SplashScreenViewModel by viewModels()
    private var binding: MainScreenBinding? = null
    private val bookData = listOf(
        BookData("Ши1ийн дин ду иза",  "https://firebasestorage.googleapis.com/v0/b/zhainas-bd711.appspot.com/o/%D0%A8%D0%B8l%D0%B8%D0%B8%CC%86%D0%BD-%D0%B4%D0%B8%D0%BD-%D0%B4%D1%83-%D0%B8%D0%B7%D0%B0.pdf?alt=media&token=fac67565-f761-4dc1-b649-8b66e8aa042e", R.drawable.shiin_din_du_iza1, listOf(3, 9, 27), listOf("1. Гочдархочун дешхьалхе", "2. Ши1ийн дин ду иза", "3. Массарна а хоийла")),
        BookData("Дийцар а, масал а", "https://firebasestorage.googleapis.com/v0/b/zhainas-bd711.appspot.com/o/%D0%94%D0%B8%D0%B9%D1%86%D0%B0%D1%80-%D0%B0-%D0%BC%D0%B0%D1%81%D0%B0%D0%BB-%D0%B0.pdf?alt=media&token=b57648ac-fc5a-4eb6-b90a-c969cce27605", R.drawable.diycar_a_masal_a2, listOf(3, 5, 6, 8, 11, 13, 15, 18, 20, 22, 25, 27, 31, 34, 36, 41, 44, 47), listOf("1. Дешхьалхе", "2. Абу Бакр ас-Сиддийкъ", "3. 1умар бин аль-Хатт1аб", "4. Хьажжаж бин Юсуф ас-Сакъафий", "5. Баязийд ал-Аввал (Ткъес)", "6. Абу 1убайдат бин Жаррахь", "7. Сулайм ал-Аввал", "8. Билу Хьаьжа", "9. 1абдуррахьман бин 1умар", "10. Ал-Му1тасим Биллах1", "11. Багдадан къеда", "12. Ибну Зурайкъ Ал-Баг1дадий", "13. Хаварижийн амал", "14. Шайх аш-Ша1равий", "15. Нуруддийн Махьмуд", "16. Ц1архазмах кхийрина паччахь", "17. Мухьаммад Ал-Фатихь", "18. ")),
        BookData("Дуьххьара - 1акъидат", "https://firebasestorage.googleapis.com/v0/b/zhainas-bd711.appspot.com/o/%D0%94%D1%83%D1%8C%D1%85%D1%85%D1%8C%D0%B0%D1%80%D0%B0-%E2%80%93-I%D0%B0%D0%BA%D1%8A%D0%B8%D0%B4%D0%B0%D1%82.pdf?alt=media&token=d795d057-b40f-44d7-8bd3-e99f103c4dba", R.drawable.duhhara_akidat3, listOf(), listOf("", )),
        BookData("Берашна гулдина веа халифан дийцарш","https://firebasestorage.googleapis.com/v0/b/zhainas-bd711.appspot.com/o/%D0%91%D0%B5%D1%80%D0%B0%D1%88%D0%BD%D0%B0-%D0%B3%D1%83%D0%BB%D0%B4%D0%B8%D0%BD%D0%B0-%D0%B2%D0%B5%D0%B0-%D1%85%D0%B0%D0%BB%D0%B8%D1%84%D0%B0%D0%BD-%D0%B4%D0%B8%D0%B9%D1%86%D0%B0%D1%80%D1%88.pdf?alt=media&token=5d8ae869-8d6c-405d-8068-971d9993d931", R.drawable.berashna_guldina_diycarsh4, listOf(), listOf("", )),
        BookData("Хьалхара дакъа","https://firebasestorage.googleapis.com/v0/b/zhainas-bd711.appspot.com/o/%D0%91%D0%B5%D1%80%D0%B0%D1%88%D0%BD%D0%B0-%D0%B3%D1%83%D0%BB%D0%B4%D0%B8%D0%BD%D0%B0-%C2%AB%D0%90%D0%BB%D0%BB%D0%B0%D1%85l%D0%B0%D1%85-%D1%82%D0%B5%D1%88%D0%B0%D1%80-%D0%B4%D1%83%D1%8C%D0%B9%D1%86%D1%83%C2%BB-220-%D1%85%D0%B0%D1%82%D1%82%D0%B0%D1%80%D0%BD%D0%B0-%D0%B6%D0%BE%D0%BF.pdf?alt=media&token=430ef88e-015b-48ae-8bf0-d0261b4b5416", R.drawable.halhara_daka5, listOf(), listOf("", )),
        BookData("1акъидат 1илманан юхьиг","https://firebasestorage.googleapis.com/v0/b/zhainas-bd711.appspot.com/o/I%D0%B0%D0%BA%D1%8A%D0%B8%D0%B4%D0%B0%D1%82-I%D0%B8%D0%BB%D0%BC%D0%B0%D0%BD%D0%B0%D0%BD-%D1%8E%D1%85%D1%8C%D0%B8%D0%B3.pdf?alt=media&token=70fab2e4-9a5d-4d50-9170-6fda6dd65d3e", R.drawable.akidat_ilmanan_yuhig6, listOf(), listOf("", )),
        BookData("Марха кхабаран дозалла","https://firebasestorage.googleapis.com/v0/b/zhainas-bd711.appspot.com/o/%D0%9C%D0%B0%D1%80%D1%85%D0%B0-%D0%BA%D1%85%D0%B0%D0%B1%D0%B0%D1%80%D0%B0%D0%BD-%D0%B4%D0%BE%D0%B7%D0%B0%D0%BB%D0%BB%D0%B0.pdf?alt=media&token=cc998b94-3453-4bf4-b422-b46044ea0b61", R.drawable.marha_kabaran_dozalla7, listOf(), listOf("", )),
        BookData("Мархийн баттахь ян мегар йолу 173 дика 1амал","https://firebasestorage.googleapis.com/v0/b/zhainas-bd711.appspot.com/o/%D0%9C%D0%B0%D1%80%D1%85%D0%B8%D0%B9%D0%BD-%D0%B1%D0%B0%D1%82%D1%82%D0%B0%D1%85%D1%8C-%D1%8F%D0%BD-%D0%BC%D0%B5%D0%B3%D0%B0%D1%80-%D0%B9%D0%BE%D0%BB%D1%83-173-%D0%B4%D0%B8%D0%BA%D0%B0-I%D0%B0%D0%BC%D0%B0%D0%BB.pdf?alt=media&token=064458b5-3e70-4747-b0b3-648fd7fe2661", R.drawable.marhiyn_battah_173_amal8, listOf(), listOf("", )),
        BookData("Кхо бух а, церан тоьшаллаш а","https://firebasestorage.googleapis.com/v0/b/zhainas-bd711.appspot.com/o/%D0%9A%D1%85%D0%BE-%D0%B1%D1%83%D1%85-%D0%B0-%D1%86%D0%B5%D1%80%D0%B0%D0%BD-%D1%82%D0%BE%D1%8C%D1%88%D0%B0%D0%BB%D0%BB%D0%B0%D1%88-%D0%B0.pdf?alt=media&token=b4e8d894-7d7c-4979-b921-6a118804ae71", R.drawable.ko_buh_a_ceran_toshallash_a9, listOf(), listOf("", )),
        BookData("Коронавирус (Нохчийн маттахь)","https://firebasestorage.googleapis.com/v0/b/zhainas-bd711.appspot.com/o/%D0%9A%D0%BE%D1%80%D0%BE%D0%BD%D0%B0%D0%B2%D0%B8%D1%80%D1%83%D1%81.pdf?alt=media&token=eaa21ee2-d471-40ec-91c7-5c35b1e10cd0", R.drawable.coronavirus_chechen10, listOf(), listOf("", )),
        BookData("Коронавирус (Оьрсийн маттахь)","https://firebasestorage.googleapis.com/v0/b/zhainas-bd711.appspot.com/o/%D0%9A%D0%BE%D1%80%D0%BE%D0%BD%D0%B0%D0%B2%D0%B8%D1%80%D1%83%D1%81-%D0%9E%D1%8C%D1%80%D1%81%D0%B8%D0%B9%D0%BD-%D0%BC%D0%B0%D1%82%D1%82%D0%B0%D1%85%D1%8C.pdf?alt=media&token=00f121cb-6fb5-41eb-a515-fd05cb283066", R.drawable.coronavirus_russian11, listOf(), listOf("", ))
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.isLoading.value
            }
        }
        setContentView(R.layout.main_screen)
        binding = MainScreenBinding.bind(findViewById(R.id.rootMainScreen))

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding?.apply {
            recyclerView.layoutManager = layoutManager
            val adapter = BookRecyclerViewAdapter(bookData)
            recyclerView.adapter = adapter
        }
    }
}