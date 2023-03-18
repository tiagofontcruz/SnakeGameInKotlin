import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

import javax.swing.ImageIcon
import javax.swing.JPanel
import javax.swing.Timer


class Board : JPanel(), ActionListener {

    private val boardWidth = 600
    private val boardHeight = 600
    private val dotSize = 20
    private val boardSize = 1800
    private val randPos = 29
    private val speed = 140

    private val axisX = IntArray(boardSize)
    private val axisY = IntArray(boardSize)

    private var bodyPieces: Int = 0
    private var foodScore = 0
    private var foodX: Int = 0
    private var foodY: Int = 0

    private var rightDirection = true
    private var leftDirection = false
    private var upDirection = false
    private var downDirection = false
    private var isRunning = true

    private var timer: Timer? = null
    private var bodyImage: Image? = null
    private var foodImage: Image? = null
    private var headImage: Image? = null

    init {

        addKeyListener(TAdapter())
        background = Color.black
        isFocusable = true
        preferredSize = Dimension(boardWidth, boardHeight)
        loadImages()
        initGame()
    }

    private fun loadImages() {

        val body = ImageIcon("src/jvmMain/resources/body.png")
        bodyImage = body.image
        val food = ImageIcon("src/jvmMain/resources/food.png")
        foodImage = food.image
        val head = ImageIcon("src/jvmMain/resources/head.png")
        headImage = head.image
    }

    private fun initGame() {

        bodyPieces = 3
        for (index in 0 until bodyPieces) {
            axisX[index] = 100 - index * dotSize
            axisY[index] = 100
        }
        foodRespawn()
        timer = Timer(speed, this)
        timer!!.start()
    }

    public override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        drawGame(g)
    }

    private fun drawGame(g: Graphics) {

        if (isRunning) {
            g.drawImage(foodImage, foodX, foodY, this)
            for (index in 0 until bodyPieces) {
                if (index == 0) {
                    g.drawImage(headImage, axisX[index], axisY[index], this)
                } else {
                    g.drawImage(bodyImage, axisX[index], axisY[index], this)
                }
            }

            val message = "Score: " + this.foodScore


            val medium = Font("Helvetica", Font.BOLD, 24)
            val fontMetrics = getFontMetrics(medium)
            val rendering = RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

            rendering[RenderingHints.KEY_RENDERING] = RenderingHints.VALUE_RENDER_QUALITY

            (g as Graphics2D).setRenderingHints(rendering)

            g.color = Color.white
            g.font = medium
            g.drawString(message, (boardWidth - fontMetrics.stringWidth(message)) / 2, boardHeight / 10)

            Toolkit.getDefaultToolkit().sync()
        } else {
            gameOver(g)
        }
    }

    private fun gameOver(g: Graphics) {

        val message = "Game Over"
        val medium = Font("Helvetica", Font.BOLD, 24)
        val fontMetrics = getFontMetrics(medium)
        val rendering = RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        rendering[RenderingHints.KEY_RENDERING] = RenderingHints.VALUE_RENDER_QUALITY

        (g as Graphics2D).setRenderingHints(rendering)

        g.color = Color.white
        g.font = medium
        g.drawString(message, (boardWidth - fontMetrics.stringWidth(message)) / 2, boardHeight / 2)
    }

    private fun checkFood() {

        if (axisX[0] == foodX && axisY[0] == foodY) {
            foodScore++
            bodyPieces++
            foodRespawn()
        }
    }

    private fun move() {

        for (index in bodyPieces downTo 1) {
            axisX[index] = axisX[index - 1]
            axisY[index] = axisY[index - 1]
        }

        if (leftDirection) {
            axisX[0] -= dotSize
        }
        if (rightDirection) {
            axisX[0] += dotSize
        }
        if (upDirection) {
            axisY[0] -= dotSize
        }
        if (downDirection) {
            axisY[0] += dotSize
        }
    }

    private fun checkCollisions() {

        for (index in bodyPieces downTo 1) {
            if (index > 4 && axisX[0] == axisX[index] && axisY[0] == axisY[index]) {
                isRunning = false
            }
        }

        if (axisY[0] >= boardHeight) {
            isRunning = false
        }
        if (axisY[0] < 0) {
            isRunning = false
        }
        if (axisX[0] >= boardWidth) {
            isRunning = false
        }
        if (axisX[0] < 0) {
            isRunning = false
        }
        if (!isRunning) {
            timer!!.stop()
        }
    }

    private fun foodRespawn() {

        val randAxisX = (Math.random() * randPos).toInt()
        foodX = randAxisX * dotSize
        val randAxisY = (Math.random() * randPos).toInt()
        foodY = randAxisY * dotSize
    }

    override fun actionPerformed(e: ActionEvent) {

        if (isRunning) {
            checkFood()
            checkCollisions()
            move()
        }
        repaint()
    }

    private inner class TAdapter : KeyAdapter() {

        override fun keyPressed(e: KeyEvent?) {

            val key = e!!.keyCode
            if (key == KeyEvent.VK_LEFT && !rightDirection) {
                leftDirection = true
                upDirection = false
                downDirection = false
            }
            if (key == KeyEvent.VK_RIGHT && !leftDirection) {
                rightDirection = true
                upDirection = false
                downDirection = false
            }
            if (key == KeyEvent.VK_UP && !downDirection) {
                upDirection = true
                rightDirection = false
                leftDirection = false
            }
            if (key == KeyEvent.VK_DOWN && !upDirection) {
                downDirection = true
                rightDirection = false
                leftDirection = false
            }
        }
    }
}