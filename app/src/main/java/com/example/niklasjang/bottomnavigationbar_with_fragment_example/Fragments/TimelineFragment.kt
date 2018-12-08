package com.example.niklasjang.bottomnavigationbar_with_fragment_example.Fragments


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.ShadowDrawableWrapper
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.niklasjang.bottomnavigationbar_with_fragment_example.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.post_row.view.*
import android.view.animation.LinearInterpolator
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import com.example.niklasjang.bottomnavigationbar_with_fragment_example.Activitys.*
import com.example.niklasjang.bottomnavigationbar_with_fragment_example.Models.Key
import com.example.niklasjang.bottomnavigationbar_with_fragment_example.Models.ShowInfor
import com.example.niklasjang.bottomnavigationbar_with_fragment_example.Models.ShowInfor2
import com.google.firebase.database.*


class TimelineFragment : Fragment() {
    companion object {
        val USER_KEY = "USER_KEY"
        val POST_KEY = "POST_KEY"
    }

    var btnFilter: Button? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timeline, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("TEST 123 $Coin")
        fetchPost()
        Process_Show()

        btnFilter = view.findViewById<Button>(R.id.btnFilter)
        btnFilter?.setOnClickListener {
            val intent = Intent(activity, FilterActivity::class.java)
            startActivityForResult(intent, 100)
        }


    }

    /*
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
                //TODO 필터 구현
            }
        }
    */
    fun fetchPost() {

        val ref = FirebaseDatabase.getInstance().getReference("posts")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerview_timeline)
                recyclerView?.adapter = adapter
                List.clear()

                for (h in p0.children) {
                    val hero = h.getValue(Post::class.java)
                    List.add(hero!!)
                }
                List.reverse()
                for (h in List) {
                    adapter.add(UserItem(h))
                }

                //UI : divider는 post_backgroudn에서 지정했음
//                recyclerView?.invalidateItemDecorations()
//                recyclerView?.addItemDecoration(DividerItemDecoration(view?.context, DividerItemDecoration.VERTICAL))
                /*
                p0.children.forEach {
                    //print All data at /posts in firebase
                    Log.d("MakePost", "here!@#${it}")

                    //savePostToFirebaseDatabase에서 setValue한 형식대로 get을 한다.
                    val post = it.getValue(Post::class.java)
                    if (post != null) {
                        adapter.add(UserItem(post))
                    }
                }
                */
//                adapter.notifyDataSetChanged()
                Log.d("FetchPost", "${adapter.itemCount}")

                //각 post들을 클릭했을 때 나오는 화면
                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    var pass = 1
                    val List : MutableList<Post>

                    List= mutableListOf()

                    val ref = FirebaseDatabase.getInstance().getReference("posts/${item.post.postname}/Show_User_id")

                    ref.addValueEventListener(object : ValueEventListener{

                        override fun onDataChange(p0: DataSnapshot) {
                            List.clear()
                            for(h in p0.children){
                                val value=h.value.toString()
                                println("TEST POST $value $Id")
                                if(value.equals(Id.toString())){
                                       pass=0

                                   }
                                }
                            if (pass == 0) {
                                val intent = Intent(view.context, PostLogActivity::class.java)
                                intent.putExtra(POST_KEY, userItem.post)
                                startActivity(intent)
                            } else if (Coin >= 10) {
                                Third_Check = 1
                                Coin -= 5
                                val Hash = Show_ref.push().key
                                val Info = ShowInfor(id = Id.toString(), check = 0, hashID = Hash!!)
                                Show_ref.child(Hash!!).setValue(Info)

                                Show_User_ref.child("${userItem.post.postname}")
                                    .child("Show_User_id")
                                    .child("${UserId}")
                                    .setValue("$Id")

                                val intent = Intent(view.context, PostLogActivity::class.java)
                                intent.putExtra(POST_KEY, userItem.post)
                                startActivity(intent)
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {

                        }
                    })



                }

            }

            override fun onCancelled(p0: DatabaseError) {
                //Toast.make ~~
            }
        })
    }

}

private fun Process_Show() { //개발자에게 만 주어지는 소스, 즉시 처리(게시물 볼 때)
    Show_ref.addValueEventListener(object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onDataChange(p0: DataSnapshot) {
            for (h in p0.children) {
                val hero = h.getValue(ShowInfor::class.java)
                Sub_List.clear()
                if (hero!!.check == 0) {

                    Sub_List.add(hero!!)
                }

            }
            Key_Save_ref.addValueEventListener(object : ValueEventListener {

                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    println("TEST 2 $Third_Check")


                    Key_List.clear()
                    for (h in p0.children) {
                        val hero = h.getValue(Key::class.java)
                        Key_List.add(hero!!)

                    }
                }
            })

            for (h in Sub_List) {
                for (h2 in Key_List) {
                    if (h.id == h2.id) {

                        if (Third_Check == 0) {
                            return
                        }

                        val hero1 = Key(id = h2.id, uid = h2.uid, coin = h2.coin - 5, hashID = h2.hashID)
                        Key_List.set(h2.id.toInt() - 1, hero1)
                        Key_Save_ref.child(h2.hashID).setValue(hero1)
                        val hero2 = ShowInfor(h.id, 1, h.hashID)
                        Show_ref.child(h.hashID).setValue(hero2)

                    }
                }
            }
            Third_Check = 0
        }
    })
}

//Item은  com.xwray.groupie에 정의된 타입으로  그냥 받아들이면 됨
class UserItem(val post: Post) : Item<ViewHolder>() {
    //여기서 return한 layout 파일의 형식대로 recycler view에 추가됨.
    override fun getLayout(): Int {
        return R.layout.post_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        //viewHolder.itemView까지 하면 view를 얻는다고 보면 됨.
        viewHolder.itemView.tvLectureName_post_row.text = "[${post.lecturename}]"
        viewHolder.itemView.tvProfessorName_post_row.text = "[${post.professorName}]"

        //TODO 사진 업로드. 프로필 이미지 업로드 이렇게 하면 됨.
        //Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.ivPostImage)
    }

}


@Parcelize
class Post(
    var postname: String,
    var lecturename: String,
    var professorName: String,
    var year: Int,
    var test: Int,
    var service: Int,
    var reward: Double,
    var vote: Int,
    var uid: String,
    var contents: String,
    var Id: Int
) : Parcelable {
    constructor() : this(
        "postName", "과목명",
        "교수님", -1, -1, -1, -1.0, 0, "", "",0
    )
}
