package sg.searchhouse.agentconnect.helper

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

// Usage:
// 1. Specify event data type. rf. model/event/AppEvent
// 2. Register disposable (listener) on the intended lifecycle owner that you want it to be listen on. rf. BaseActivity
// 3. Send event via RxBus.publish({your event object}) from your event source.
// 4. Remember to dispose the disposable on destroy.
object RxBus {
    private val publisher = PublishSubject.create<Any>()

    // Send event
    fun publish(event: Any) {
        publisher.onNext(event)
    }

    // Receive and handle event
    fun <T> listen(eventType: Class<T>): Observable<T> = publisher.ofType(eventType)


}