package com.example.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tictactoe.ui.theme.TicTacToeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class Win {
    PLAYER,
    AI,
    DRAW
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TicTacToeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TTTScreen()
                }
            }
        }
    }
}

@Composable
fun TTTScreen() {
    // true - player's turn, false - AI's turn
    val playerTurn = remember { mutableStateOf(true) }
    // true - player move, false - AI move, null - no move
    val moves = remember {
        mutableStateListOf<Boolean?>(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
    }

    val win = remember { mutableStateOf<Win?>(null) }

    val tapPos: (Offset) -> Unit = {
        if (playerTurn.value && win.value == null) {
            val x = (it.x / 200).toInt()
            val y = (it.y / 200).toInt()
            val posInMoves = (x * 3) + y

            if (moves[posInMoves] == null) {
                moves[posInMoves] = true
                playerTurn.value = false
                win.value = checkEndGame(moves)
            }
        }
    }

//    val updatedOnTap = rememberUpdatedState(onTap)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Tic Tac Toe", fontSize = 30.sp, modifier = Modifier.padding(16.dp))

        Header(playerTurn.value)

        Board(moves, tapPos)

        if (!playerTurn.value && win.value == null) {
            CircularProgressIndicator(color = Color.Red, modifier = Modifier.padding(16.dp))

            val coroutineScope = rememberCoroutineScope()
            LaunchedEffect(key1 = Unit) {
                coroutineScope.launch {
                    delay(1500L)
                    while (true) {
                        val i = Random.nextInt(9)
                        if (moves[i] == null) {
                            moves[i] = false
                            playerTurn.value = true
                            win.value = checkEndGame(moves)
                            break
                        }
                    }
                }
            }
        }

        if (win.value != null) {
            when (win.value) {
                Win.PLAYER -> {
                    Text(text = "Player won", fontSize = 25.sp)
                }
                Win.AI -> {
                    Text(text = "AI won", fontSize = 25.sp)
                }
                Win.DRAW -> {
                    Text(text = "Draw! Play again.", fontSize = 25.sp)
                }

                else -> {}
            }
        }

        Button(onClick = {
            playerTurn.value = true
            win.value = null
            for (i in 0..8) {
                moves[i] = null
            }
        }) {
            Text(text = "Click to start over")
        }
    }
}

fun checkEndGame(m: List<Boolean?>): Win? {
    var win: Win? = null

    if (
        (m[0] == true && m[1] == true && m[2] == true) ||
        (m[3] == true && m[4] == true && m[5] == true) ||
        (m[6] == true && m[7] == true && m[8] == true) ||
        (m[0] == true && m[3] == true && m[6] == true) ||
        (m[1] == true && m[4] == true && m[7] == true) ||
        (m[2] == true && m[5] == true && m[8] == true) ||
        (m[0] == true && m[4] == true && m[8] == true) ||
        (m[2] == true && m[4] == true && m[6] == true)
    ) {
        win = Win.PLAYER
    }

    if (
        (m[0] == false && m[1] == false && m[2] == false) ||
        (m[3] == false && m[4] == false && m[5] == false) ||
        (m[6] == false && m[7] == false && m[8] == false) ||
        (m[0] == false && m[3] == false && m[6] == false) ||
        (m[1] == false && m[4] == false && m[7] == false) ||
        (m[2] == false && m[5] == false && m[8] == false) ||
        (m[0] == false && m[4] == false && m[8] == false) ||
        (m[2] == false && m[4] == false && m[6] == false)
            ) {
        win = Win.AI
    }

    if(win == null) {
        var available = false
        for (i in 0..8) {
            if (m[i] == null) {
                available = true
            }
        }
        if (!available) {
            win = Win.DRAW
        }
    }

    return win
}

@Composable
fun Header(playerTurn: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        val playerBoxColor = if (playerTurn) Color.Blue else Color.LightGray
        val aiBoxColor = if (playerTurn) Color.LightGray else Color.Red

        Box(
            modifier = Modifier
                .width(100.dp)
                .background(playerBoxColor)
        ) {
            Text(
                text = "Player", modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.width(50.dp))

        Box(
            modifier = Modifier
                .width(100.dp)
                .background(aiBoxColor)
        ) {
            Text(
                text = "AI", modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
fun Board(moves: List<Boolean?>, onTap: (Offset) -> Unit) {

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(32.dp)
            .background(Color.LightGray)
//            .clickable { onTap }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = onTap
                )
            }
        ) {
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxSize(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .height(2.dp)
                            .fillMaxWidth(1f)
                            .background(Color.Black)
                    ) { }
                    Row(
                        modifier = Modifier
                            .height(2.dp)
                            .fillMaxWidth(1f)
                            .background(Color.Black)
                    ) { }
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxSize(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .width(2.dp)
                            .fillMaxHeight(1f)
                            .background(Color.Black)
                    ) { }
                    Column(
                        modifier = Modifier
                            .width(2.dp)
                            .fillMaxHeight(1f)
                            .background(Color.Black)
                    ) { }
                }
                Column(modifier = Modifier.fillMaxSize(1f)) {
                    for (i in 0..2) {
                        Row(modifier = Modifier.weight(1f)) {
                            for (j in 0..2) {
                                Column(modifier = Modifier.weight(1f)) {
                                    getComposableFromMove(move = moves[i * 3 + j])
                                }
                            }
                        }
                    }
                }
        }
    }

    @Composable
    fun getComposableFromMove(move: Boolean?) {
        when (move) {
            true -> Image(
                painter = painterResource(id = R.drawable.ic_x),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(1f),
                colorFilter = ColorFilter.tint(Color.Blue)
            )

            false -> Image(
                painter = painterResource(id = R.drawable.ic_o),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(1f),
                colorFilter = ColorFilter.tint(Color.Red)
            )

            null -> Image(
                painter = painterResource(id = R.drawable.ic_null),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(1f)
            )
        }
    }
