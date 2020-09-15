package com.example.drawingcanvas

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

// Drawing view for creating a canvas with attributes and default color and stroke size.

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    //object of class CustomPath which is nullable
    private var dDrawPath : CustomPath? = null

    private var dCanvasBitmap : Bitmap? = null
    private var dDrawPaint: Paint? = null
    private var dCanvasPaint: Paint? = null
    private var dBrushSize: Float = 0.toFloat()
    private var color = Color.BLACK
    private var canvas : Canvas? = null
    /*arraylist to store the paths so if we release the touch
     the path stays on the screen, and we can draw more along with it.
    */
    private val dPaths = ArrayList<CustomPath>()
    private val dUndoPaths = ArrayList<CustomPath>()
    //private val mRedoPaths = ArrayList<CustomPath>()

    init {
        setUpDrawing()
    }

    fun onClickUndo(){
        if(dPaths.size > 0){
            dUndoPaths.add(dPaths.removeAt(dPaths.size -1))
            invalidate()
        }
    }

    fun onClickRedo(){
        if(dUndoPaths.size > 0){
            dPaths.add(dUndoPaths.removeAt(dUndoPaths.size -1))
            invalidate()
        }
    }

    fun onReset(){
        if(dPaths.size > 0){
            dPaths.clear()
            invalidate()
        }
    }
    //Initialise the attributes of the view for Drawing class
    private fun setUpDrawing(){
        dDrawPaint = Paint()
        dDrawPath = CustomPath(color, dBrushSize)
        dDrawPaint!!.color = color
        //draw a Stroke style
        dDrawPaint!!.style = Paint.Style.STROKE
        dDrawPaint!!.strokeJoin = Paint.Join.ROUND
        dDrawPaint!!.strokeCap = Paint.Cap.ROUND

        dCanvasPaint = Paint(Paint.DITHER_FLAG)
        dBrushSize = 20.toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        dCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(dCanvasBitmap!!)
    }
    //called when a stroke is drawn on the canvas
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(dCanvasBitmap!!, 0f, 0f, dCanvasPaint)

        for(path in dPaths){
            dDrawPaint!!.strokeWidth = path.brushThickness
            dDrawPaint!!.color = path.color
            canvas.drawPath(path, dDrawPaint!!)
        }

        if(!dDrawPath!!.isEmpty){
            dDrawPaint!!.strokeWidth = dDrawPath!!.brushThickness
            dDrawPaint!!.color = dDrawPath!!.color
            canvas.drawPath(dDrawPath!!, dDrawPaint!!)
        }
    }
    //event listener when a touch event occurs
    override fun onTouchEvent(event: MotionEvent): Boolean {
        //x coordinate
        val touchX = event.x
        //y coordinate
        val touchY = event.y

        when(event.action){
            MotionEvent.ACTION_DOWN -> {
                dDrawPath!!.color = color
                dDrawPath!!.brushThickness = dBrushSize

                dDrawPath!!.reset() //clear any lines or curves from the path
                //Set the beginning of next to point(x, y)
                dDrawPath!!.moveTo(
                    touchX,
                    touchY
                )
            }
            //Add a line from last point to the specified point (x,y)
            MotionEvent.ACTION_MOVE -> {
                dDrawPath!!.lineTo(
                    touchX,
                    touchY
                )
            }
            MotionEvent.ACTION_UP -> {
                dPaths.add(dDrawPath!!)
                dDrawPath = CustomPath(color, dBrushSize)
            }
            else -> return false
        }
        invalidate()
        return true
    }

    fun setSizeForBrush(newSize : Float){
        dBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, resources.displayMetrics)
        dDrawPaint!!.strokeWidth = dBrushSize
    }

    fun setColor(newColor: String){
        color = Color.parseColor(newColor)
        dDrawPaint!!.color = color
    }

    internal inner class CustomPath(var color: Int, var brushThickness: Float) : Path(){

    }


}