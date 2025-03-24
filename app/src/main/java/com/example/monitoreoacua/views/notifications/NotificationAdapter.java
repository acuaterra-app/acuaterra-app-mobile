package com.example.monitoreoacua.views.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Notification;
import com.example.monitoreoacua.utils.DateUtils;
import com.example.monitoreoacua.utils.NotificationStyleUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    private List<Notification> notifications;
    private Context context;
    private OnNotificationClickListener notificationClickListener;
    private boolean isLoadingMore = false;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
    }

    public NotificationAdapter(Context context, OnNotificationClickListener listener) {
        this.context = context;
        this.notifications = new ArrayList<>();
        this.notificationClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycle_view_item_notification, parent, false);
            return new NotificationViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycle_view_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NotificationViewHolder) {
            NotificationViewHolder viewHolder = (NotificationViewHolder) holder;
            Notification notification = notifications.get(position);
            
            viewHolder.titleTextView.setText(notification.getTitle());
            viewHolder.messageTextView.setText(notification.getMessage());
            
            // Format and display date
            if (notification.getData() != null && notification.getData().getDateHour() != null) {
                String formattedDate = DateUtils.formatDateForDisplay(notification.getData().getDateHour());
                viewHolder.dateTextView.setText(formattedDate);
            } else {
                viewHolder.dateTextView.setText("");
            }
            
            // Show indicator for unread notifications
            // Show indicator for unread notifications
            if (notification.isUnread()) {
                viewHolder.unreadIndicator.setVisibility(View.VISIBLE);
                
                // Get message type or use default
                String messageType = "info";
                if (notification.getData() != null && 
                    notification.getData().getMetaData() != null && 
                    notification.getData().getMetaData().containsKey("messageType")) {
                    messageType = notification.getData().getMetaData().get("messageType");
                }
                
                // Apply appropriate color to the unread indicator based on message type
                NotificationStyleUtils.applyUnreadIndicatorColor(viewHolder.unreadIndicator, messageType);
            } else {
                viewHolder.unreadIndicator.setVisibility(View.GONE);
            }
            // Set click listener
            viewHolder.cardView.setOnClickListener(v -> {
                if (notificationClickListener != null) {
                    notificationClickListener.onNotificationClick(notification);
                }
            });
            
            // Set notification type icon based on metadata
            if (notification.getData() != null && 
                notification.getData().getMetaData() != null && 
                notification.getData().getMetaData().containsKey("messageType")) {
                
                String messageType = notification.getData().getMetaData().get("messageType");
                switch (Objects.requireNonNull(messageType)) {
                    case "info":
                        viewHolder.typeIcon.setImageResource(R.drawable.ic_info);
                        break;
                    case "warning":
                        viewHolder.typeIcon.setImageResource(R.drawable.ic_warning);
                        break;
                    case "error":
                        viewHolder.typeIcon.setImageResource(R.drawable.ic_error);
                        break;
                    default:
                        viewHolder.typeIcon.setImageResource(R.drawable.ic_notification);
                        break;
                }
                
                // Set icon tint color based on notification type
                viewHolder.typeIcon.setColorFilter(NotificationStyleUtils.getTextColor(messageType));
                
                // Apply styling to card and text views based on notification type
                NotificationStyleUtils.applyStyleToCardView(viewHolder.cardView, messageType);
                NotificationStyleUtils.applyTextColor(messageType, 
                    viewHolder.titleTextView, viewHolder.messageTextView, viewHolder.dateTextView);
            } else {
                viewHolder.typeIcon.setImageResource(R.drawable.ic_notification);
                
                // Apply default "info" styling for notifications without a type
                String defaultType = "info";
                viewHolder.typeIcon.setColorFilter(NotificationStyleUtils.getTextColor(defaultType));
                NotificationStyleUtils.applyStyleToCardView(viewHolder.cardView, defaultType);
                NotificationStyleUtils.applyTextColor(defaultType, 
                    viewHolder.titleTextView, viewHolder.messageTextView, viewHolder.dateTextView);
            }
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == notifications.size() - 1 && isLoadingMore) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }

    public void addNotifications(List<Notification> newNotifications) {
        int startPosition = notifications.size();
        this.notifications.addAll(newNotifications);
        notifyItemRangeInserted(startPosition, newNotifications.size());
    }

    public void setLoadingMore(boolean loadingMore) {
        if (this.isLoadingMore != loadingMore) {
            this.isLoadingMore = loadingMore;
            if (loadingMore) {
                notifications.add(null);
                notifyItemInserted(notifications.size() - 1);
            } else {
                int loadingIndex = notifications.indexOf(null);
                if (loadingIndex > -1) {
                    notifications.remove(loadingIndex);
                    notifyItemRemoved(loadingIndex);
                }
            }
        }
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void clearNotifications() {
        int size = notifications.size();
        notifications.clear();
        notifyItemRangeRemoved(0, size);
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView messageTextView;
        TextView dateTextView;
        View unreadIndicator;
        CardView cardView;
        ImageView typeIcon;

        NotificationViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_notification_title);
            messageTextView = itemView.findViewById(R.id.text_notification_message);
            dateTextView = itemView.findViewById(R.id.text_notification_date);
            unreadIndicator = itemView.findViewById(R.id.view_notification_unread_indicator);
            cardView = (CardView) itemView; // The CardView is the root view of the layout
            typeIcon = itemView.findViewById(R.id.image_notification_type);
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }
}

