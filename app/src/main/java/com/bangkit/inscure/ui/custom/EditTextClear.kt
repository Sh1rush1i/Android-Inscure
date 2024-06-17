package com.bangkit.inscure.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.bangkit.inscure.R

class EditTextClear : AppCompatEditText, View.OnTouchListener {

    private lateinit var clearButtonImage: Drawable
    private var startDrawable: Drawable? = null

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        if (attrs != null) {
            val attributes = context.obtainStyledAttributes(attrs, R.styleable.TextFieldClear)
            startDrawable = attributes.getDrawable(R.styleable.TextFieldClear_drawableStart)
            attributes.recycle()
        }

        // Set padding between the icon and text
        compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen.icon_padding)

        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_clear_24) as Drawable
        setOnTouchListener(this)
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) showClearButton() else hideClearButton()
            }

            override fun afterTextChanged(s: Editable) {
                // Nothing
            }
        })

        startDrawable?.let { setButtonDrawables(startOfTheText = it) }
    }

    private fun showClearButton() {
        setButtonDrawables(startOfTheText = startDrawable, endOfTheText = clearButtonImage)
    }

    private fun hideClearButton() {
        setButtonDrawables(startOfTheText = startDrawable)
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (clearButtonImage.intrinsicWidth + paddingStart).toFloat()
                if (event.x < clearButtonEnd) isClearButtonClicked = true
            } else {
                clearButtonStart = (width - paddingEnd - clearButtonImage.intrinsicWidth).toFloat()
                if (event.x > clearButtonStart) isClearButtonClicked = true
            }
            if (isClearButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_clear_24) as Drawable
                        showClearButton()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_clear_24) as Drawable
                        text?.clear()
                        hideClearButton()
                        return true
                    }
                    else -> return false
                }
            }
        }
        return false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        context.apply {
            background = ContextCompat.getDrawable(this, R.drawable.custom_form_input)
            setTextColor(ContextCompat.getColor(this, R.color.primary_dark))
            setHintTextColor(ContextCompat.getColor(this, R.color.secondary_light))
        }
        isSingleLine = true
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}