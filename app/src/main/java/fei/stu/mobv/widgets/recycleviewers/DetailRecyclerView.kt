package fei.stu.mobv.widgets.recycleviewers

import android.content.Context
import android.util.AttributeSet
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fei.stu.mobv.widgets.adapters.DetailAdapter
import fei.stu.mobv.widgets.items.BarDetailItem

class DetailRecyclerView : RecyclerView {
    private lateinit var detailAdapter: DetailAdapter

    /**
     * Default constructor
     *
     * @param context context for the activity
     */
    constructor(context: Context) : super(context) {
        init(context)
    }

    /**
     * Constructor for XML layout
     *
     * @param context activity context
     * @param attrs   xml attributes
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context, VERTICAL, false)
        detailAdapter = DetailAdapter()
        adapter = detailAdapter
    }
}

@BindingAdapter(value = ["detail_items"])
fun DetailRecyclerView.applyDetailItems(
    details: List<BarDetailItem>?
) {
    (adapter as DetailAdapter).items = details ?: emptyList()
}