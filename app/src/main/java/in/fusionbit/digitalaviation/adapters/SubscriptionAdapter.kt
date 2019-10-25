package `in`.fusionbit.digitalaviation.adapters

import `in`.fusionbit.digitalaviation.R
import `in`.fusionbit.digitalaviation.model.SubscriptionsModel
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView

class SubscriptionAdapter(private val list: ArrayList<SubscriptionsModel>) :
    RecyclerView.Adapter<SubscriptionAdapter.VHSubscription>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHSubscription {
        return VHSubscription(
            LayoutInflater.from(parent.context).inflate(
                R.layout.single_subscription_view,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: VHSubscription, position: Int) {
        holder.tvName.text = list[position].courseName
        holder.tvDate.text = "Subscribed On : " + list[position].date
        holder.tvPrice.text = "Price : " + list[position].coursePrice + " INR"
        holder.tvExpireDate.text = "Subscription Expires On : " + list[position].expiredDate

        if (list[position].expired == "1") {
            holder.tvExpireDate.text = "Subscription Expired"
            holder.tvExpireDate.setTextColor(Color.RED)

        }

    }

    class VHSubscription(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName = itemView.findViewById<AppCompatTextView>(R.id.tv_course_name)
        val tvDate = itemView.findViewById<AppCompatTextView>(R.id.tv_subscription_date)
        val tvPrice = itemView.findViewById<AppCompatTextView>(R.id.tv_subscription_price)
        val tvExpireDate = itemView.findViewById<AppCompatTextView>(R.id.tv_subscription_expire)
    }
}