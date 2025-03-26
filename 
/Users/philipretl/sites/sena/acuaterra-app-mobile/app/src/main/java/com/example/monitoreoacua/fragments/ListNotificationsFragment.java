    /**
     * Common method to fetch notifications with pagination
     * @param page The page number to fetch
     * @param isInitialFetch Whether this is an initial fetch (true) or loading more (false)
     */
    private void fetchNotificationsPage(int page, boolean isInitialFetch) {
        if (isInitialFetch) {
            showLoading();
            notificationsList.clear();
        } else {
            isLoading = true;
            notificationAdapter.setLoadingMore(true);
        }

        ApiNotificationsService apiService = ApiClient.getClient().create(ApiNotificationsService.class);
        ListNotificationRequest listNotificationRequest = new ListNotificationRequest();

        // Add pagination parameters if needed
        Map<String, String> queryParams = new HashMap<>();
        if (!isInitialFetch) {
            queryParams.put("page", String.valueOf(page));
            queryParams.put("limit", String.valueOf(ITEMS_PER_PAGE));
            
            if (!searchQuery.isEmpty()) {
                queryParams.put("search", searchQuery);
            }
        }

        apiService.getNotifications(listNotificationRequest.getAuthToken()).enqueue(new Callback<ListNotificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<ListNotificationResponse> call, @NonNull Response<ListNotificationResponse> response) {
                if (isInitialFetch) {
                    hideLoading();
                } else {
                    isLoading = false;
                    notificationAdapter.setLoadingMore(false);
                }

                if (isAdded()) { // Check if fragment is still attached to activity
                    if (response.isSuccessful() && response.body() != null) {
                        ListNotificationResponse notificationResponse = response.body();
                        List<Notification> notifications = notificationResponse.getAllNotification();

                        if (notifications != null && !notifications.isEmpty()) {
                            if (isInitialFetch) {
                                notificationsList.addAll(notifications);
                                notificationAdapter.setNotifications(notificationsList);
                                showContent();
                            } else {
                                int prevSize = notificationsList.size();
                                notificationsList.addAll(notifications);
                                notificationAdapter.setNotifications(notificationsList);
                            }

                            // Check pagination info
                            if (notificationResponse.getMeta() != null &&
                                    notificationResponse.getMeta().getPagination() != null) {
                                ApiResponse.Meta.Pagination pagination = notificationResponse.getMeta().getPagination();
                                currentPage = pagination.getCurrentPage();
                                hasMorePages = pagination.isHasNext();
                            } else if (!isInitialFetch) {
                                hasMorePages = false;
                            }
                        } else {
                            if (isInitialFetch) {
                                showEmptyState(getString(R.string.no_notifications_found));
                            } else {
                                hasMorePages = false;
                            }
                        }
                    } else {
                        if (isInitialFetch) {
                            handleApiError(response);
                        } else {
                            hasMorePages = false;
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ListNotificationResponse> call, @NonNull Throwable t) {
                if (isInitialFetch) {
                    hideLoading();
                    if (isAdded()) {
                        showError("Error de conexión: " + t.getLocalizedMessage());
                    }
                } else {
                    isLoading = false;
                    notificationAdapter.setLoadingMore(false);
                    currentPage--; // Revert page increment since request failed
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Error loading more notifications", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void fetchNotifications() {
        currentPage = 1;
        hasMorePages = true;
        fetchNotificationsPage(currentPage, true);
    }
    private void loadMoreNotifications() {
        if (isLoading || !hasMorePages) return;

        currentPage++;
        fetchNotificationsPage(currentPage, false);
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
        List<Notification> currentList = new ArrayList<>(notificationAdapter.getNotifications());
        isAscending = !isAscending;
        notificationAdapter.setNotifications(currentList);
