package com.github.jamilligioielli.cryptomonitor

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.jamilligioielli.cryptomonitor.ui.theme.CryptomonitorTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptomonitorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TextLayout(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Composable
fun TextLayout(modifier: Modifier = Modifier) {
    var dataTextState by remember { mutableStateOf("dd/mm/yyyy hh:mm:ss") }
    var valorTextState by remember { mutableStateOf("R$0,00") }
    val context = LocalContext.current

    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = "Cotação - BITCOIN",
                fontSize = 20.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = valorTextState,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = dataTextState,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier,
                        verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally)
             {
                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.Main).launch {
                            try {
                                val service = MercadoBitcoinServiceFactory().create()
                                val response = service.getTicker()

                                if (response.isSuccessful) {
                                    val tickerResponse = response.body()

                                    val lastValue = tickerResponse?.ticker?.last?.toDoubleOrNull()
                                    if (lastValue != null) {
                                        val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                                        valorTextState = numberFormat.format(lastValue)
                                    }

                                    val date = tickerResponse?.ticker?.date?.let { Date(it * 1000L) }
                                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                                    dataTextState = sdf.format(date)

                                } else {
                                    val errorMessage = when (response.code()) {
                                        400 -> "Bad Request"
                                        401 -> "Unauthorized"
                                        403 -> "Forbidden"
                                        404 -> "Not Found"
                                        else -> "Unknown error"
                                    }
                                    showMessage(context, errorMessage)
                                }

                            } catch (e: Exception) {
                                println(e.message)
                                showMessage(context, "${e.message}")
                            }
                        }
                    },
                    modifier
                        .width(120.dp)
                        .wrapContentHeight()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue
                    ),
                    shape =  CircleShape
                ) {
                    Text(
                        text = "Atualizar",
                        color = Color.White
                    )
                }
            }

        }

    }

}

fun showMessage(context: Context, message:String){
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Preview(showBackground = true)
@Composable
fun PreviewCardMensagem() {
    CryptomonitorTheme {
        TextLayout()
    }
}