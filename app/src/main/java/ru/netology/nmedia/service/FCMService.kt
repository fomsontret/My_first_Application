package ru.netology.nmedia.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.JsonParser
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {

    private val action = "action"
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.channel_remote_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.channel_remote_description)
            }

            val manager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        android.util.Log.d("FCM_DEBUG", "Push получен: ${message.data}")

        val rawContent = message.data[content]

        // Сервер может прислать recipientId отдельным полем
        // либо внутри JSON в поле content.
        val json = rawContent?.let {
            runCatching {
                JsonParser.parseString(it).asJsonObject
            }.getOrNull()
        }

        val recipientId = message.data["recipientId"]?.toLongOrNull()
            ?: json?.get("recipientId")
                ?.takeUnless { it.isJsonNull }
                ?.let { element ->
                    runCatching { element.asLong }.getOrNull()
                }

        val currentId = AppAuth.getInstance().authStateFlow.value.id

        android.util.Log.d(
            "FCM_DEBUG",
            "recipientId=$recipientId, currentId=$currentId"
        )

        // Если recipientId отсутствует — массовая рассылка.
        // Если recipientId не совпадает с текущим ID —
        // повторно отправляем FCM-токен и не показываем уведомление.
        if (recipientId != null && recipientId != currentId) {
            android.util.Log.d(
                "FCM_DEBUG",
                "ID не совпали — повторно отправляем FCM-токен"
            )

            AppAuth.getInstance().sendPushToken()
            return
        }

        val actionValue = message.data[action] ?: return

        val parsedAction = runCatching {
            Action.valueOf(actionValue)
        }.getOrNull() ?: return

        when (parsedAction) {
            Action.LIKE -> {
                val like = runCatching {
                    gson.fromJson(rawContent, Like::class.java)
                }.getOrNull() ?: return

                handleLike(like)
            }

            Action.NEW_POST -> {
                val post = runCatching {
                    gson.fromJson(rawContent, NewPost::class.java)
                }.getOrNull() ?: return

                handleNewPost(post)
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        android.util.Log.d("FCM_TOKEN", "Получен новый FCM-токен")

        AppAuth.getInstance().sendPushToken(token)
    }

    private fun handleNewPost(content: NewPost) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("${content.userName} опубликовал новый пост:")
            .setContentText(content.postContent)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(content.postContent)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notify(notification)
    }

    private fun handleLike(content: Like) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_liked,
                    content.userName,
                    content.postAuthor
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notify(notification)
    }

    private fun notify(notification: Notification) {
        if (
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this)
                .notify(Random.nextInt(100_000), notification)
        }
    }
}

enum class Action {
    LIKE,
    NEW_POST,
}

data class NewPost(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postContent: String,
)

data class Like(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
)