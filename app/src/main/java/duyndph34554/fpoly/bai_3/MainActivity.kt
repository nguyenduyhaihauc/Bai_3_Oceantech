package duyndph34554.fpoly.bai_3

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlarmScreen()
        }
    }
}

// Hien thi giao dien bao thuc
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AlarmScreen() {
    val context = LocalContext.current
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

//    Luu thoi gian duoc chon va trang thai lap laij bao thuc
    var selectedTime by remember {
        mutableStateOf(Calendar.getInstance())
    }
    var isRepeating by remember {
        mutableStateOf(false)
    }

    Scaffold(
//        topBar = {
//            TopAppBar(title = {
//                Text(text = "Alarm App")
//            })
//        },
        content = {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(10.dp))

//                Compoment chon thoi gian bao thuc
                TimePicker(selectedTime = selectedTime, onTimeSelected = { newTime ->
                    selectedTime = newTime
                })

                Spacer(modifier = Modifier.height(10.dp))

//                Checkbox chon lap lai bao thuc
                RepeatCheckbox(isRepeating = isRepeating, onCheckedChange = { isChecked ->
                    isRepeating = isChecked
                })

                Spacer(modifier = Modifier.height(10.dp))

//                Button de dat bao thuc
                SetAlarmButton(
                    context = context,
                    alarmManager = alarmManager,
                    selectedTime = selectedTime,
                    isRepeating = isRepeating)

            }
        }
    )
}

// Composable TimePicker chon gio va phut
@Composable
fun TimePicker(selectedTime: Calendar, onTimeSelected: (Calendar) -> Unit) {
    val context = LocalContext.current
    var hour by remember {
        mutableIntStateOf(selectedTime.get(Calendar.HOUR_OF_DAY))
    }
    var minute by remember {
        mutableIntStateOf(selectedTime.get(Calendar.MINUTE))
    }

//    giao dien chon gio va phut
    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Select Alarm Time",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row (
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Hour: ")

            Spacer(modifier = Modifier.width(6.dp))

            OutlinedTextField(
                value = hour.toString(),
                onValueChange = {},
                enabled = false,
                modifier = Modifier.width(60.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(text = "Minute: ")

            Spacer(modifier = Modifier.width(6.dp))

            OutlinedTextField(
                value = minute.toString(),
                onValueChange = {},
                enabled = false,
                modifier = Modifier.width(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

//        Buttton mo Dialog chon gio phut
        Button(onClick = {
            showTimePickerDialog(context) {newHour, newMinute ->
                hour = newHour
                minute = newMinute
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                onTimeSelected(calendar)
            }
        }) {
            Text(text = "Set Time")
        }
    }
}

//Compoable Chexbox chon lap lai bao thuc
@Composable
fun RepeatCheckbox(isRepeating: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row (
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isRepeating,
            onCheckedChange = {isChecked ->
                onCheckedChange(isChecked)
            }
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(text = "Repeat",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

//Composable Button dat bao thuc
@Composable
fun SetAlarmButton(
    context: Context,
    alarmManager: AlarmManager,
    selectedTime: Calendar,
    isRepeating: Boolean
) {
    Button(onClick = {
        setAlarm(context, alarmManager, selectedTime, isRepeating)
    }) {
        Text(text = "Set Alarm")
    }
}

//Hien thi Dialog chon gio phut
@Suppress("NAME_SHADOWING")
private fun showTimePickerDialog(context: Context, onTimeSet: (Int, Int) -> Unit) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

//    Tao va hien thi Dialog TimePicker
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            onTimeSet(hourOfDay, minute)
        },
        hour,
        minute,
        true
    )

    timePickerDialog.show()
}

//Ham setAlarm dat bao thuc
@SuppressLint("ScheduleExactAlarm")
private fun setAlarm(
    context: Context,
    alarmManager: AlarmManager,
    selectedTime: Calendar,
    isRepeating: Boolean
) {
    val intent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

//    Dat bao thuc lai hoac 1 lan
    if (isRepeating) {
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            selectedTime.timeInMillis,
            AlarmManager.INTERVAL_HOUR,
            pendingIntent
        )
    } else {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            selectedTime.timeInMillis,
            pendingIntent
        )
    }

//    Hien thi thong bao bao thuc da duoc dat
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val formattedTime = dateFormat.format(selectedTime.time)
    Toast.makeText(context, "Alarm set for $formattedTime", Toast.LENGTH_SHORT).show()
}