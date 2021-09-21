package sg.searchhouse.agentconnect.enumeration.status

import sg.searchhouse.agentconnect.model.api.ApiError

class ApiStatus<T> private constructor(
    val key: StatusKey,
    val body: T? = null,
    val error: ApiError? = null,
    private val pendingRequests: Int = 0
) {
    /**
     * Refer doc of `loadingInstanceCore`
     */
    fun getUpdatedPendingRequests(newStatusKey: StatusKey): Int {
        return if (key == newStatusKey) {
            pendingRequests + 1
        } else {
            pendingRequests
        }
    }

    /**
     * Refer doc of `loadingInstanceCore`
     */
    fun isRequestRepeated(): Boolean {
        return pendingRequests > 1
    }

    companion object {
        fun <T> loadingInstance(existingApiStatus: ApiStatus<T>? = null): ApiStatus<T> {
            return loadingInstanceCore(StatusKey.LOADING, existingApiStatus)
        }

        fun <T> loadingNextInstance(existingApiStatus: ApiStatus<T>? = null): ApiStatus<T> {
            return loadingInstanceCore(StatusKey.LOADING_NEXT_LIST_ITEM, existingApiStatus)
        }

        /**
         *  `existingApiStatus`: Needed for case of repeated calls of same endpoint in short time
         *  e.g. multiple check boxes
         *  Upon receiving API response, check `isRequestRepeated()`. If true, ignore handle response.
         */
        private fun <T> loadingInstanceCore(
            newStatusKey: StatusKey,
            existingApiStatus: ApiStatus<T>? = null
        ): ApiStatus<T> {
            val pendingRequest =
                existingApiStatus?.getUpdatedPendingRequests(newStatusKey) ?: 1
            return ApiStatus(newStatusKey, null, null, pendingRequest)
        }

        fun <T> successInstance(body: T?): ApiStatus<T> {
            return ApiStatus(StatusKey.SUCCESS, body, null)
        }

        fun <T> failInstance(error: ApiError): ApiStatus<T> {
            return ApiStatus(StatusKey.FAIL, null, error)
        }

        fun <T> errorInstance(): ApiStatus<T> {
            return ApiStatus(StatusKey.ERROR, null, null)
        }

        val showOccupiedStatuses = listOf(StatusKey.SUCCESS, StatusKey.LOADING_NEXT_LIST_ITEM)
    }

    enum class StatusKey {
        LOADING,
        LOADING_NEXT_LIST_ITEM,
        SUCCESS,
        FAIL,
        ERROR
    }
}