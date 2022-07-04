package com.example.avatwin.Fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import com.example.avatwin.Auth.AuthInterceptor
import com.example.avatwin.DataClass.myAvatarRes
import com.example.avatwin.Fragment.Team.TeamMemberFragment
import com.example.avatwin.R
import com.example.avatwin.Service.AvatarService
import kotlinx.android.synthetic.main.activity_login_register.*
import kotlinx.android.synthetic.main.fragment_avatar_register.*
import kotlinx.android.synthetic.main.fragment_mypage.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class AvatarRegisterFragment2  : Fragment(){
    private val OPEN_GALLERY = 1
    private val REQUEST_IMAGE_CAPTURE = 2
    private val GALLERY_ACCESS_REQUEST_CODE = 1001
    var profileUri: Uri? = null
    lateinit var currentPhotoPath : String //문자열 형태의 사진 경로값 (초기값을 null로 시작하고 싶을 때 - lateinti var)
    val REQUEST_IMAGE_PICK = 10
    var m_imageFile: File? = null
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
        register_avatar_style.setOnCheckedChangeListener{ group, checkedId ->
            when(checkedId){
                R.id.cartoon -> avatar_style = "cartoon"

                R.id.arcane -> avatar_style = "arcane"

                R.id.caricature -> avatar_style = "caricature"

                R.id.anime -> avatar_style = "anime"
            }
        }


        register_avatar_button.setOnClickListener{
            registerAvatar(register_avatar_name.getText().toString(),register_avatar_description.getText().toString(),avatar_style,register_avatar_styleid.getText().toString().toLong())
        }

    }
    fun registerAvatar(avatarName: String,avatarDescription: String, avatarStyle: String, avatarStyleId:Long){
        try {

            val okHttpClient = OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build()
            var retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(AvatarService.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create()).build()

            var apiService = retrofit.create(AvatarService::class.java)

            val absPath = absolutelyPath(profileUri!!)
            Log.e("absPath",absPath)
            val file = File(absPath)

            val requestBodyProfile = RequestBody.create(
                MediaType.parse("multipart/jpeg"), file
            )
            val requestBodyProfile2 = RequestBody.create(
                MediaType.parse("multipart/jpeg"), m_imageFile
            )
            val multipartBodyProfile = MultipartBody.Part.createFormData(
                "originfile", file.name, requestBodyProfile
            )
            val multipartBodyProfile2 = MultipartBody.Part.createFormData(
                "createdfile", file.name, requestBodyProfile2
            )
            apiService.post_avatar(
                multipartBodyProfile,
                multipartBodyProfile2, avatarName, avatarDescription, avatarStyle, avatarStyleId
            ).enqueue(object : Callback<myAvatarRes> {
                override fun onResponse(call: Call<myAvatarRes>, response: Response<myAvatarRes>) {
                    val newProfileUpdataResult = response.body()
                    val fragmentA = MyPageFragment()
                    val bundle = Bundle()
                    fragmentA.arguments=bundle
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.add(R.id.container,fragmentA)
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
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,profileUri)
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
                }catch (e:Exception){
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
        return File.createTempFile("JPEG_${timestamp}_",".jpeg",storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    //갤러리에 저장
    private fun savePhoto(bitmap: Bitmap) {
        //사진 폴더에 저장하기 위한 경로 선언
        val folderPath = Environment.getExternalStorageDirectory().absolutePath + "/Pictures/"
        val timestamp : String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "${timestamp}.jpeg"
        val folder = File(folderPath)
        if(!folder.isDirectory){//해당 경로에 폴더가 존재하지
            folder.mkdir() // make directory의 줄임말로 해당경로에 폴더 자동으로
        }
        //실제적인 저장 처리
        val out = FileOutputStream(folderPath + fileName)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)

    }
    private fun requestPermission(){
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA),1)

    }
    private fun checkPermission():Boolean{

        return (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)

    }
}