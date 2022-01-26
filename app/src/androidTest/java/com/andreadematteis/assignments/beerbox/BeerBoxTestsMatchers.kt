package com.andreadematteis.assignments.beerbox

import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher


object BeerBoxTestsMatchers {
    fun withDrawable(resourceId: Int): Matcher<View?> {
        return DrawableMatcher(resourceId)
    }

    fun noDrawable(): Matcher<View?> {
        return DrawableMatcher(-1)
    }

    class DrawableMatcher(private val resourceId: Int) : TypeSafeMatcher<View?>() {
        override fun matchesSafely(item: View?): Boolean {
            if(item !is ImageView) {
                return false
            }

            if(resourceId < 0){
                return item.drawable == null
            }

            item.context?.getDrawable(resourceId)?.let {
                val expectedBitmap = it.toBitmap()
                return item.drawable?.toBitmap()?.sameAs(expectedBitmap) ?: (resourceId < 0)
            }

            return resourceId < 0
        }

        override fun describeTo(description: Description?) {
            description?.appendText("ImageView with drawable same as drawable with id $resourceId")
        }
    }

    fun atPosition(position: Int, itemMatcher: Matcher<View?>): Matcher<View?> =
        object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("Has item at position $position: ")
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder = view.findViewHolderForAdapterPosition(position)
                    ?: // has no item on such position
                    return false
                return itemMatcher.matches(viewHolder.itemView)
            }
        }

    fun atPositionOnView(
        position: Int, itemMatcher: Matcher<View?>,
        targetViewId: Int
    ): Matcher<View?> {
        return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has view id $itemMatcher at position $position")
            }

            override fun matchesSafely(recyclerView: RecyclerView): Boolean {
                val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
                val targetView = viewHolder!!.itemView.findViewById<View>(targetViewId)
                return itemMatcher.matches(targetView)
            }
        }
    }
}