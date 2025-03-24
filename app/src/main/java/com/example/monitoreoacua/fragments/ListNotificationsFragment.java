package com.example.monitoreoacua.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.monitoreoacua.R;
import com.example.monitoreoacua.business.models.Notification;
import com.example.monitoreoacua.service.ApiClient;
import com.example.monitoreoacua.service.ApiNotificationsService;
import com.example.monitoreoacua.service.request.ListNotificationRequest;
import com.example.monitoreoacua.service.response.ApiResponse;
import com.example.monitoreoacua.service.response.ListNotificationResponse;
import com.example.monitoreoacua.views.notifications.NotificationAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment for displaying and managing notifications
 */
/**
 * Fragment for displaying and managing notifications
 */
public class ListNotificationsFragment extends Fragment {

    private static final String TAG = "ListNotificationsFrag";
    private static final int ITEMS_PER_PAGE = 10;

    private RecyclerView recyclerViewNotifications;
    private NotificationAdapter notificationAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView tvEmptyView;
    private EditText editTextSearch;
    private AppCompatImageButton buttonSortByDate;

    private List<Notification> notificationsList = new ArrayList<>();
    private boolean isAscending = false;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean hasMorePages = true;
    private String searchQuery = "";

    private OnNotificationSelectedListener notificationSelectedListener;

    public interface OnNotificationSelectedListener {
        void onNotificationSelected(Notification notification);
    }
    
    public ListNotificationsFragment() {
        // Required empty public constructor
    }

    public static ListNotificationsFragment newInstance() {
        return new ListNotificationsFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            notificationSelectedListener = (OnNotificationSelectedListener) context;
        } catch (ClassCastException e) {
            Log.e(TAG, "Activity must implement OnNotificationSelectedListener", e);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewNotifications = view.findViewById(R.id.recyclerViewNotifications);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmptyView = view.findViewById(R.id.tvEmptyView);
        editTextSearch = view.findViewById(R.id.searchNotifications);
        buttonSortByDate = view.findViewById(R.id.buttonSortNotifications);

        // Setup RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewNotifications.setLayoutManager(layoutManager);
        
        // Initialize adapter with the standalone implementation
        notificationAdapter = new NotificationAdapter(requireContext(), notification -> {
            if (notificationSelectedListener != null) {
                notificationSelectedListener.onNotificationSelected(notification);
            } else {
                Toast.makeText(getContext(), "Error: La actividad no implementa OnNotificationSelectedListener", Toast.LENGTH_LONG).show();
            }
        });
        recyclerViewNotifications.setAdapter(notificationAdapter);

        // Pull to refresh
        swipeRefreshLayout.setOnRefreshListener(this::refreshNotifications);

        // Sort button
        buttonSortByDate.setOnClickListener(v -> sortNotificationsByDate());

        // Search functionality
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString();
                filterNotifications(searchQuery);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Pagination scroll listener
        recyclerViewNotifications.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) { // Scrolling down
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && hasMorePages) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                && firstVisibleItemPosition >= 0
                                && totalItemCount >= ITEMS_PER_PAGE) {
                            // Load more items
                            loadMoreNotifications();
                        }
                    }
                }
            }
        });

        // Initial data load
        fetchNotifications();
    }

    private void fetchNotifications() {
        showLoading();
        currentPage = 1;
        hasMorePages = true;
        notificationsList.clear();

        ApiNotificationsService apiService = ApiClient.getClient().create(ApiNotificationsService.class);
        ListNotificationRequest listNotificationRequest = new ListNotificationRequest();


        apiService.getNotifications(listNotificationRequest.getAuthToken()).enqueue(new Callback<ListNotificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<ListNotificationResponse> call, @NonNull Response<ListNotificationResponse> response) {
                hideLoading();

                if (isAdded()) { // Check if fragment is still attached to activity
                    if (response.isSuccessful() && response.body() != null) {
                        ListNotificationResponse notificationResponse = response.body();
                        List<Notification> notifications = notificationResponse.getAllNotification();

                        if (notifications != null && !notifications.isEmpty()) {
                            notificationsList.addAll(notifications);
                            notificationAdapter.setNotifications(notificationsList);

                            // Check pagination info
                            if (notificationResponse.getMeta() != null &&
                                    notificationResponse.getMeta().getPagination() != null) {
                                ApiResponse.Meta.Pagination pagination = notificationResponse.getMeta().getPagination();
                                currentPage = pagination.getCurrentPage();
                                hasMorePages = pagination.isHasNext();
                            }

                            showContent();
                        } else {
                            showEmptyState(getString(R.string.no_notifications_found));
                        }
                    } else {
                        handleApiError(response);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ListNotificationResponse> call, @NonNull Throwable t) {
                hideLoading();
                if (isAdded()) {
                    showError("Error de conexión: " + t.getLocalizedMessage());
                }
            }
        });
    }

    private void loadMoreNotifications() {
        if (isLoading || !hasMorePages) return;

        isLoading = true;
        currentPage++;

        // Show a loading indicator at the bottom of the list
        notificationAdapter.setLoadingMore(true);

        ApiNotificationsService apiService = ApiClient.getClient().create(ApiNotificationsService.class);
        ListNotificationRequest listNotificationRequest = new ListNotificationRequest();

        // Assuming the API supports pagination parameters
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("page", String.valueOf(currentPage));
        queryParams.put("limit", String.valueOf(ITEMS_PER_PAGE));

        if (!searchQuery.isEmpty()) {
            queryParams.put("search", searchQuery);
        }

        // This is a placeholder - you'll need to update the API interface to support pagination params
        apiService.getNotifications(listNotificationRequest.getAuthToken()).enqueue(new Callback<ListNotificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<ListNotificationResponse> call, @NonNull Response<ListNotificationResponse> response) {
                isLoading = false;
                notificationAdapter.setLoadingMore(false);

                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    ListNotificationResponse notificationResponse = response.body();
                    List<Notification> newNotifications = notificationResponse.getAllNotification();

                    if (newNotifications != null && !newNotifications.isEmpty()) {
                        int prevSize = notificationsList.size();
                        notificationsList.addAll(newNotifications);
                        notificationAdapter.setNotifications(notificationsList);

                        // Check pagination info
                        if (notificationResponse.getMeta() != null &&
                                notificationResponse.getMeta().getPagination() != null) {
                            ApiResponse.Meta.Pagination pagination = notificationResponse.getMeta().getPagination();
                            currentPage = pagination.getCurrentPage();
                            hasMorePages = pagination.isHasNext();
                        } else {
                            hasMorePages = false;
                        }
                    } else {
                        hasMorePages = false;
                    }
                } else {
                    hasMorePages = false;
                }
            }

            @Override
            public void onFailure(@NonNull Call<ListNotificationResponse> call, @NonNull Throwable t) {
                isLoading = false;
                notificationAdapter.setLoadingMore(false);
                currentPage--; // Revert page increment since request failed
                Toast.makeText(getContext(), "Error loading more notifications", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void refreshNotifications() {
        swipeRefreshLayout.setRefreshing(true);
        fetchNotifications();
    }

    private void filterNotifications(String query) {
        if (notificationsList.isEmpty()) return;

        if (query.isEmpty()) {
            notificationAdapter.setNotifications(notificationsList);
            return;
        }

        List<Notification> filteredList = new ArrayList<>();
        for (Notification notification : notificationsList) {
            if (notification.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    notification.getMessage().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(notification);
            }
        }

        notificationAdapter.setNotifications(filteredList);

        if (filteredList.isEmpty()) {
            showEmptyState("No se encontraron notificaciones que coincidan con la búsqueda");
        } else {
            showContent();
        }
    }

    private void sortNotificationsByDate() {
        if (notificationsList.isEmpty()) return;

        List<Notification> currentList = new ArrayList<>(notificationAdapter.getNotifications());

        // Sort by dateHour from the notification data
        Collections.sort(currentList, new Comparator<Notification>() {
            @Override
            public int compare(Notification n1, Notification n2) {
                String date1 = n1.getData().getDateHour();
                String date2 = n2.getData().getDateHour();

                if (isAscending) {
                    return date1.compareTo(date2);
                } else {
                    return date2.compareTo(date1);
                }
            }
        });

        isAscending = !isAscending;
        notificationAdapter.setNotifications(currentList);
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmptyView.setVisibility(View.GONE);
        recyclerViewNotifications.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showContent() {
        recyclerViewNotifications.setVisibility(View.VISIBLE);
        tvEmptyView.setVisibility(View.GONE);
    }

    private void showEmptyState(String message) {
        recyclerViewNotifications.setVisibility(View.GONE);
        tvEmptyView.setVisibility(View.VISIBLE);
        tvEmptyView.setText(message);
    }

    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        showEmptyState(getString(R.string.error_loading_notifications));
    }

    private void handleApiError(Response<ListNotificationResponse> response) {
        String errorMessage;
        try {
            errorMessage = response.errorBody() != null ?
                    response.errorBody().string() : "Unknown error";
        } catch (Exception e) {
            errorMessage = "Error " + response.code();
        }

        showError("Error: " + errorMessage);
    }
}
