package com.mengyuan1998.finger_dancing.adpter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;


import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.mengyuan1998.finger_dancing.R;
import com.mengyuan1998.finger_dancing.Utilities.ConversationMessage;
import com.mengyuan1998.finger_dancing.Utilities.MessageManager;
import com.mengyuan1998.finger_dancing.Utilities.SignMessage;
import com.mengyuan1998.finger_dancing.Utilities.TextMessage;
import com.mengyuan1998.finger_dancing.Utilities.VoiceMessage;
import com.mengyuan1998.finger_dancing.Utilities.auto_complete.SimpleAutocompleteCallback;
import com.mengyuan1998.finger_dancing.Utilities.auto_complete.SimplePolicy;
import com.mengyuan1998.finger_dancing.Utilities.auto_complete.SimpleRecyclerViewPresenter;
import com.otaliastudios.autocomplete.Autocomplete;
import com.otaliastudios.autocomplete.AutocompleteCallback;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import okhttp3.MediaType;

import static android.content.ContentValues.TAG;

/**
 * Created by Scarecrow on 2018/2/10.
 */

public class ConversationMessagesRVAdapter extends RecyclerView.Adapter<ConversationMessagesRVAdapter.MessagesItemViewHolder> {


    private static final MediaType MEDIA_TYPE_JSON
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    private List<ConversationMessage> messages_list;

    private static Context context;

    public ConversationMessagesRVAdapter(Context context) {
        this.context = context;
        if(context == null)
            Log.d(TAG, "ConversationMessagesRVAdapter: oh noooooooooo");
        Log.d(TAG, "ConversationMessagesRVAdapter: start");
        updateMessageList();
    }

    public void updateMessageList() {
        messages_list = MessageManager.getInstance().getMessagesList();
    }

    static class MessagesItemViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout receive_msg_view, send_msg_view,
                sign_confirm_dialog, sign_recapture_dialog;

        public TextView  send_msg_content,
                sign_confirm_yes_button, sign_confirm_no_button,
                sign_recapture_yes_button, sign_recapture_no_button,
                msg_type_display;
        public EditText msg_content_receive;

        public Context context2;


        public MessagesItemViewHolder(View view, Context context2) {
            super(view);
            this.context2 = context2;
            receive_msg_view = view.findViewById(R.id.message_receive_view);
            send_msg_view = view.findViewById(R.id.message_send_view);
            sign_confirm_dialog = view.findViewById(R.id.sign_confirm_dialog);
            sign_recapture_dialog = view.findViewById(R.id.sign_recapture_dialog);

            msg_content_receive = view.findViewById(R.id.msg_content_receive);

            //setAutocomplete(msg_content_receive, context);

            send_msg_content = view.findViewById(R.id.msg_content_send);

            sign_confirm_yes_button = view.findViewById(R.id.button_sign_confirm_yes);
            sign_confirm_no_button = view.findViewById(R.id.button_sign_confirm_no);

            sign_recapture_yes_button = view.findViewById(R.id.button_sign_recapture_yes);
            sign_recapture_no_button = view.findViewById(R.id.button_sign_recapture_no);

            msg_type_display = view.findViewById(R.id.text_view_msg_type_display);

        }

        private void setAutocomplete(EditText textView, Context context){
            //TODO  添加自动补全
            AutocompleteCallback temp = new SimpleAutocompleteCallback();
            Drawable backgroundDrawable = new ColorDrawable(Color.WHITE);
            float elevation = 6f;
            Autocomplete.on(textView)
                    .with(new SimplePolicy())
                    .with(temp)
                    .with(elevation)
                    .with(backgroundDrawable)
                    .with(new SimpleRecyclerViewPresenter(context))
                    .build();
        }

    }

    @Override
    public MessagesItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.conversation_message_item, parent, false);
        return new MessagesItemViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(final MessagesItemViewHolder holder, int position) {
        ConversationMessage message = messages_list.get(position);
        initializeHolderView(holder);
        // 每个手语是否已经重发保存一个状态 保存于数据中
        // view是重用的 每次都要先初始化  然后跟着数据的情况改变
        switch (message.getMsgType()) {
            case ConversationMessage.SIGN:
                final SignMessage signMessage = (SignMessage) message;
                //如果这条手势消息没有被确认 则保持可被确认的初始状态 随时被请求重新采集
                setHolderViewByMsgState(holder, signMessage);
                break;

            case ConversationMessage.TEXT:
                TextMessage text_message = (TextMessage) message;
                holder.send_msg_view.setVisibility(View.VISIBLE);
                holder.receive_msg_view.setVisibility(View.GONE);
                holder.send_msg_content.setText(text_message.getTextContent());
                holder.msg_type_display.setText("文字消息");
                break;

            case ConversationMessage.VOICE:
                VoiceMessage voice_message = (VoiceMessage) message;
                holder.send_msg_view.setVisibility(View.VISIBLE);
                holder.receive_msg_view.setVisibility(View.GONE);
                holder.send_msg_content.setText(voice_message.getTextContent());
                holder.msg_type_display.setText("语音消息");
                break;
            default:
                break;
        }

    }

    private void initializeHolderView(MessagesItemViewHolder holder) {
        holder.send_msg_view.setVisibility(View.GONE);
        holder.receive_msg_view.setVisibility(View.GONE);
        holder.sign_recapture_dialog.setVisibility(View.GONE);
        holder.sign_confirm_dialog.setVisibility(View.GONE);
        int init_blue_color_value = 0xFF3F51B5;
        holder.sign_confirm_yes_button.setTextColor(init_blue_color_value);
        holder.sign_confirm_no_button.setTextColor(init_blue_color_value);
        holder.sign_recapture_yes_button.setTextColor(init_blue_color_value);
        holder.sign_recapture_no_button.setTextColor(init_blue_color_value);
        View.OnClickListener empty = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        };
        holder.sign_recapture_no_button.setOnClickListener(empty);
        holder.sign_recapture_yes_button.setOnClickListener(empty);
        holder.sign_confirm_no_button.setOnClickListener(empty);
        holder.sign_confirm_yes_button.setOnClickListener(empty);

    }

    private void setHolderViewByMsgState(final MessagesItemViewHolder holder,
                                         final SignMessage message) {
        initializeHolderView(holder);
        switch (message.getSignFeedbackStatus()) {
            case SignMessage.INITIAL:
                holder.receive_msg_view.setVisibility(View.VISIBLE);
                if(context == null){
                    Log.d(TAG, "setHolderViewByMsgState: it's null");
                }
                if(holder.context2 == null){
                    Log.d(TAG, "setHolderViewByMsgState: it's null too");
                }
                Log.d(TAG, "setHolderViewByMsgState: " + message.getTextContent());
                MessageManager.getInstance().synthesizeVoice(message.getTextContent());
                holder.msg_content_receive.setText(message.getTextContent());

                if (message.isCaptureComplete()) {
                    holder.sign_confirm_dialog.setVisibility(View.VISIBLE);
                    holder.sign_confirm_yes_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.sign_confirm_yes_button.setTextColor(Color.GRAY);
                            message.setSignFeedbackStatus(SignMessage.CONFIRMED_CORRECT);
                            //recognizeResultFeedback(message, true);
                            setHolderViewByMsgState(holder, message);
                        }
                    });

                    holder.sign_confirm_no_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            message.setSignFeedbackStatus(SignMessage.CONFIRMED_WRONG);
                            //recognizeResultFeedback(message, false);
                            setHolderViewByMsgState(holder, message);
                        }
                    });
                }
                break;
            case SignMessage.CONFIRMED_CORRECT:
                holder.receive_msg_view.setVisibility(View.VISIBLE);
                holder.sign_confirm_dialog.setVisibility(View.VISIBLE);
                holder.msg_content_receive.setText(message.getTextContent());
                holder.sign_confirm_yes_button.setTextColor(Color.GRAY);
                break;
            case SignMessage.CONFIRMED_WRONG:
                holder.receive_msg_view.setVisibility(View.VISIBLE);
                holder.sign_confirm_dialog.setVisibility(View.VISIBLE);
                holder.msg_content_receive.setText(message.getTextContent());
                holder.sign_confirm_no_button.setTextColor(Color.GRAY);

                holder.sign_recapture_dialog.setVisibility(View.VISIBLE);
                holder.sign_recapture_yes_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: 手语re采集 回调");
                        if (!recaptureRequest(message)) {
                            Toast.makeText(context, " 已经有一条消息在进行手语采集了，请勿重复", Toast.LENGTH_SHORT)
                                    .show();
                            return;
                        }
                        holder.sign_recapture_yes_button.setTextColor(Color.GRAY);
                        message.setSignFeedbackStatus(SignMessage.INITIAL);
                        setHolderViewByMsgState(holder, message);

                    }
                });
                holder.sign_recapture_no_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.sign_recapture_no_button.setTextColor(Color.GRAY);
                        message.setSignFeedbackStatus(SignMessage.NO_RECAPTURE);
                        setHolderViewByMsgState(holder, message);
                    }
                });
                break;
            case SignMessage.NO_RECAPTURE:
                holder.receive_msg_view.setVisibility(View.VISIBLE);
                holder.sign_confirm_dialog.setVisibility(View.VISIBLE);
                holder.msg_content_receive.setText(message.getTextContent());
                holder.sign_confirm_no_button.setTextColor(Color.GRAY);

                holder.sign_recapture_dialog.setVisibility(View.VISIBLE);
                holder.sign_recapture_no_button.setTextColor(Color.GRAY);
                break;
        }
    }

    private boolean recaptureRequest(SignMessage msg) {
        //TODO 补全原来的逻辑
        /*return MessageManager.getInstance()
                .recaptureSignRequest(msg);*/
        return false;
    }

    /*private void recognizeResultFeedback(final SignMessage msg, final boolean correctness) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();

                String capture_id = String.valueOf(msg.getCaptureId()),
                        correctness_str = correctness ? "True" : "False";

                String content = "?" + "capture_id=" + capture_id
                        + "&correctness=" + correctness_str;

                RequestBody requestBody = RequestBody
                        .create(MEDIA_TYPE_JSON, content);
                Request request = new Request.Builder()
                        .url(ArmbandManager.SERVER_IP_ADDRESS + "/capture_feedback/")
                        .post(requestBody)
                        .build();
                Log.d(TAG, "recognizeResultFeedback: seed feedback: " + content);
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        main_thread_handler.obtainMessage()
                                .sendToTarget();
                    }
                } catch (Exception ee) {
                    ee.printStackTrace();
                    Log.e(TAG, "send feedback error");
                }
            }
        }).start();
    }*/

    @SuppressLint("HandlerLeak")
    private Handler main_thread_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(context, "识别结果反馈成功", Toast.LENGTH_SHORT)
                    .show();
        }
    };

    @Override
    public int getItemCount() {
        return messages_list.size();
    }

    public void onDestroy() {
        context = null;
        main_thread_handler.removeCallbacksAndMessages(null);
    }
}
