package com.example.avatwin.Fragment.Avatar

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.avatwin.Adapter.Avatar.avatarExampleAdapter
import com.example.avatwin.Auth.AuthInterceptor
import com.example.avatwin.DataClass.myAvatarRes
import com.example.avatwin.Fragment.MyPageFragment
import com.example.avatwin.R
import com.example.avatwin.Service.AvatarService
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_avatar_register.*
import kotlinx.android.synthetic.main.fragment_chat.*
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class AvatarRegisterFragment  : Fragment(){
    private val OPEN_GALLERY = 1
    private val REQUEST_IMAGE_CAPTURE = 2
    private val GALLERY_ACCESS_REQUEST_CODE = 1001
    var profileUri: Uri? = null
    lateinit var currentPhotoPath : String //문자열 형태의 사진 경로값 (초기값을 null로 시작하고 싶을 때 - lateinti var)
    val REQUEST_IMAGE_PICK = 10
    var m_imageFile: File? = null
    var creadImageFile: InputStream? = null
    var c_imageFile: File? = null
    var requestBodyProfile2:RequestBody?=null
    var sadUrl=""
    var happyUrl=""
    var winkUrl=""
    var angryUrl=""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.fragment_avatar_register, container, false)


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //카메라
        avatar_camera.setOnClickListener {
            if(checkPermission()){
                dispatchTakePictureIntent()
            }
            else{
                requestPermission()
            }
        }

        //갤러리
        avatar_gallery.setOnClickListener{
            permissionCheckAndOpenGallery()
        }


        //캐릭터 sytle
        var avatar_style = ""
        cartoon.isSelected=true
        register_avatar_style.setOnCheckedChangeListener{ group, checkedId ->
            when(checkedId){
                R.id.cartoon -> {
                    avatar_style = "cartoon"
                }

                R.id.arcane -> avatar_style = "arcane"

                R.id.caricature -> avatar_style = "caricature"

                R.id.anime -> avatar_style = "anime"
            }
        }

        //스타일 번호도 달라지게 ㅇ
        viewPager_image.adapter = avatarExampleAdapter(getCaricutures()) // 어댑터 생성
        viewPager_image.orientation = ViewPager2.ORIENTATION_HORIZONTAL // 방향을 가로로
        var styleId=0
        viewPager_image.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            // Paging 완료되면 호출
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                //Log.d("ViewPagerFragment", "Page ${position}")
                styleId=10
                register_avatar_styleid.setText("10")
            }
        })

        //아바타 변형
        avatar_created_button.setOnClickListener{
         makeAvatar(register_avatar_name.getText().toString(), avatar_style, styleId.toLong())
        }

        //이모티콘 생성
        avatar_imo_button.setOnClickListener{
           melonEmotikon()
        }

        //아바타 등록
        register_avatar_button.setOnClickListener{
            registerAvatar(register_avatar_name.getText().toString(), register_avatar_description.getText().toString(), avatar_style, register_avatar_styleid.getText().toString().toLong())
          }

    }
    //apiService.make_avatar(
//    multipartBodyProfile,"webtoon", 13,"ww"
//).en
    fun makeAvatar(avatarName: String, avatarStyle: String, avatarStyleId: Long){
        try {
            val gson = GsonBuilder()
                    .setLenient()
                    .create()
            val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
            var retrofit = Retrofit.Builder()
                .baseUrl(AvatarService.API_URL2)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create()).build()

            var apiService = retrofit.create(AvatarService::class.java)


            val requestBodyProfile = RequestBody.create(
                    MediaType.parse("image/jpeg"), m_imageFile
            )

            val multipartBodyProfile = MultipartBody.Part.createFormData(
                    "file", m_imageFile!!.name, requestBodyProfile
            )

            apiService.make_avatar(
                    multipartBodyProfile, 10
            ).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    //미리보기
                     creadImageFile = response.body()!!.byteStream()
                    val bitmap = BitmapFactory.decodeStream(creadImageFile)
                    avatar_created_image.setImageBitmap(bitmap)

                    //파일생성
                    val photoFile : File? = try{
                        createImageFile()
                    }catch (e: Exception){
                        null
                    }
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                                requireContext(),
                                "com.example.avatwin.fileprovider",
                                it
                        )
                        c_imageFile = it
                    }

                    //파일 작성
                    var out: OutputStream? = null
                    try{
                        out = FileOutputStream(c_imageFile)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
                    }finally{
                        out?.close()
                    }


                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("avatar_create", "OnFailuer+${t.message}")
                }
            })
/*
            apiService.make_avatar(
                multipartBodyProfile, avatarStyleId, avatarName,avatarStyle
            ).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                    val file = response.body()?.byteStream()
                    val bitmap = BitmapFactory.decodeStream(file)
                    avatar_origin_image.setImageBitmap(bitmap)
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("avatar_create", "OnFailuer+${t.message}")
                }
            })*/
        }

        catch (e: IOException) {
            e.printStackTrace()
        }

    }


    fun registerAvatar(avatarName: String, avatarDescription: String, avatarStyle: String, avatarStyleId: Long){
        try {

            val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
            var retrofit = Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(AvatarService.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create()).build()

            var apiService = retrofit.create(AvatarService::class.java)


            val requestBodyProfile = RequestBody.create(
                    MediaType.parse("image/jpeg"), m_imageFile
            )
            val requestBodyProfile2 = RequestBody.create(
                    MediaType.parse("image/jpeg"), c_imageFile
            )
            val multipartBodyProfile = MultipartBody.Part.createFormData(
                    "originfile", m_imageFile!!.name, requestBodyProfile
            )
            val multipartBodyProfile2 = MultipartBody.Part.createFormData(
                    "createdfile", c_imageFile!!.name, requestBodyProfile2
            )
            val nameBody = RequestBody.create(MediaType.parse("text/plain"), "cartoon")
            apiService.post_avatar(
                    multipartBodyProfile,
                    multipartBodyProfile2, avatarName, avatarDescription,"cartoon", avatarStyleId,"d",
                "D","d","d"
            ).enqueue(object : Callback<myAvatarRes> {
                override fun onResponse(call: Call<myAvatarRes>, response: Response<myAvatarRes>) {
                    val newProfileUpdataResult = response.body()
                    Log.e("avatar",newProfileUpdataResult.toString())
                    val fragmentA = MyPageFragment()
                    val bundle = Bundle()
                    fragmentA.arguments = bundle
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.add(R.id.container, fragmentA)
                    transaction.replace(R.id.container, fragmentA.apply { arguments = bundle }).addToBackStack(null)
                    transaction.commit()
                }

                override fun onFailure(call: Call<myAvatarRes>, t: Throwable) {
                    Log.e("avatar_create", "OnFailuer+${t.message}")
                }
            })
        }

        catch (e: IOException) {
            e.printStackTrace()
        }

    }

        private fun permissionCheckAndOpenGallery() {
            val permission = ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
            )

            /* 이미 갤러리 접근을 허용 했음 */
            if(permission == PermissionChecker.PERMISSION_GRANTED){
                openGallery()
            }
            /* 갤러리 접근이 허용된 적 없음 */
            else {
                val isAccessDenied = ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE
                )

                /* 최초로 갤러리에 접근 시도 */
                if(!isAccessDenied) {
                    ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            GALLERY_ACCESS_REQUEST_CODE
                    )
                }
                /* 갤러리 접근 권한 제한됨 */
                else {
                    val dialog = AlertDialog.Builder(requireContext())
                    dialog.setTitle("갤러리 접근 권한 제한됨")
                    dialog.setMessage("  갤러리 접근을 허용해 주세요.\n\n" +
                            "  app info -> Permissions -> storage")
                    dialog.show()
                }
            }
        }

        override fun onRequestPermissionsResult(
                requestCode: Int,
                permissions: Array<out String>,
                grantResults: IntArray
        ) {
            when(requestCode) {
                /* 갤러리에 접근이 허용되면 갤러리 열기 */
                GALLERY_ACCESS_REQUEST_CODE -> {
                    if (grantResults.isNotEmpty() && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                        openGallery()
                    }

                }}
                if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(requireContext(), "권한 설정 OK", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "권한 허용 안됨", Toast.LENGTH_SHORT).show()
                }

        }

        /* 갤러리 열어서 사진 선택 */
        private fun openGallery() {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(intent, OPEN_GALLERY)
        }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if( resultCode == Activity.RESULT_OK) {
        if(requestCode == OPEN_GALLERY ) {
            profileUri = data?.data
            val absPath = absolutelyPath(profileUri!!)
            m_imageFile = File(absPath)
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, profileUri)
                avatar_origin_image.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.d("Uri", profileUri.toString())

        }
        else if( requestCode == REQUEST_IMAGE_CAPTURE)
        { m_imageFile?.let {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val source = ImageDecoder.createSource(requireActivity().contentResolver, Uri.fromFile(it))
                ImageDecoder.decodeBitmap(source)?.let {
                    avatar_origin_image.setImageBitmap(it)
                }
            } else {
                MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, Uri.fromFile(it))?.let {
                    avatar_origin_image.setImageBitmap(it)
                }}}}
        }
    }

    /* 전달된 file uri를 절대 경로로 바꿔주는 함수 */
    private fun absolutelyPath(path: Uri): String {
        var proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        var c = requireActivity().contentResolver.query(path, proj, null, null, null)!!
        var index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c.moveToFirst()

        return c.getString(index)
    }

    //카메라
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                val photoFile : File? = try{
                    createImageFile()
                }catch (e: Exception){
                    null
                }
                photoFile?.also {
                    val photoURI : Uri = FileProvider.getUriForFile(
                            requireContext(),
                            "com.example.avatwin.fileprovider",
                            it
                    )
                    m_imageFile = it
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)}
            }
        }
    }

        //이미지 파일 생성
        private fun createImageFile(): File {
            val timestamp : String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val storageDir : File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            return File.createTempFile("JPEG_${timestamp}_", ".jpeg", storageDir).apply {
                currentPhotoPath = absolutePath
            }
        }


    private fun requestPermission(){
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 1)

    }
    private fun checkPermission():Boolean{

        return (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)

    }

    //이미지 리스트
    private fun getCaricutures(): ArrayList<Int> {
        return arrayListOf<Int>(
            R.drawable.caricuture0,
            R.drawable.caricuture1,
            R.drawable.caricuture4,
            R.drawable.caricuture2,
            R.drawable.caricuture3
            )
    }

    fun melonEmotikon(){
        Glide.with(requireContext())
            .load("https://checkmatebucket.s3.ap-northeast-2.amazonaws.com/emoticons/angry_melon.png") // 불러올 이미지 url
            .into(re_i1)
        Glide.with(requireContext())
            .load("https://checkmatebucket.s3.ap-northeast-2.amazonaws.com/emoticons/happy_melon.png") // 불러올 이미지 url
            .into(re_i2)

        Glide.with(requireContext())
            .load("https://checkmatebucket.s3.ap-northeast-2.amazonaws.com/emoticons/sad_melon.png") // 불러올 이미지 url
            .into(re_i3)

        Glide.with(requireContext())
            .load("https://checkmatebucket.s3.ap-northeast-2.amazonaws.com/emoticons/wink_melon.png") // 불러올 이미지 url
            .into(re_i4)
    }

    fun makeEmotiron(){
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
        var retrofit = Retrofit.Builder()
            .baseUrl(AvatarService.API_URL2)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(ScalarsConverterFactory.create()).build()

        var apiService = retrofit.create(AvatarService::class.java)


        val requestBodyProfile = RequestBody.create(
            MediaType.parse("image/jpeg"), c_imageFile
        )

        val multipartBodyProfile = MultipartBody.Part.createFormData(
            "file", c_imageFile!!.name, requestBodyProfile
        )

        apiService.make_emoticon(
            multipartBodyProfile
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//  var sadUrl=""
//    var happyUrl=""
//    var winkUrl=""
//    var angryUrl=""
                //서버에서 아바타등록시" 이거 제거해야함
                /*
                  Glide.with(requireContext())
                                .load(url1) // 불러올 이미지 url
                                .placeholder(defaultImage) // 이미지 로딩 시작하기 전 표시할 이미지
                                .error(defaultImage) // 로딩 에러 발생 시 표시할 이미지
                                .fallback(defaultImage) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                                .circleCrop() // 동그랗게 자르기
                                .into(iv1_image)
                            Glide.with(requireContext())
                                .load(url2) // 불러올 이미지 url
                                .placeholder(defaultImage) // 이미지 로딩 시작하기 전 표시할 이미지
                                .error(defaultImage) // 로딩 에러 발생 시 표시할 이미지
                                .fallback(defaultImage) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                                .circleCrop() // 동그랗게 자르기
                                .into(iv2_image)

                            Glide.with(requireContext())
                                .load(url3) // 불러올 이미지 url
                                .placeholder(defaultImage) // 이미지 로딩 시작하기 전 표시할 이미지
                                .error(defaultImage) // 로딩 에러 발생 시 표시할 이미지
                                .fallback(defaultImage) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                                .circleCrop() // 동그랗게 자르기
                                .into(iv3_image)

                            Glide.with(requireContext())
                                .load(url4) // 불러올 이미지 url
                                .placeholder(defaultImage) // 이미지 로딩 시작하기 전 표시할 이미지
                                .error(defaultImage) // 로딩 에러 발생 시 표시할 이미지
                                .fallback(defaultImage) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                                .circleCrop() // 동그랗게 자르기
                                .into(iv4_image)
                 */
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("avatar_create", "OnFailuer+${t.message}")
            }
        })
    }
}

