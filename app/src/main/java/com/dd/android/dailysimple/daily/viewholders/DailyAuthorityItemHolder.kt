package com.dd.android.dailysimple.daily.viewholders

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.MutableLiveData
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.widget.recycler.ItemModel
import com.dd.android.dailysimple.common.widget.recycler.ViewHolder2
import com.dd.android.dailysimple.daily.DailyViewType
import com.dd.android.dailysimple.databinding.DailyAuthorityItemBinding

class DailyAuthorityItemHolder(parent: ViewGroup) :
    ViewHolder2<DailyAuthorityItemBinding, DailyAuthorityItem>(
        parent,
        R.layout.daily_authority_item,
        BR.itemModel,
        supportActionMode = false
    ) {
    init {
        itemClickListener = ::onClick
    }

    private fun onClick(view: View) {
        val context = view.context
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        (context as? Activity)?.startActivity(intent)
    }
}

data class DailyAuthorityItem(
    override val id: Long,
    val description: String
) : ItemModel {
    override val selected = MutableLiveData<Boolean>()
}