## CoPaDS Project 2
### Justin Palmer
### Jeffrey Taglic

------------------------

Implements a pub/sub system across docker nodes. 
Uses TCP connections to perform functions between publishers, subscribers, and the event manager.


_Subscribe_ - subscriber nodes can subscribe to topics directly, or through keyword.


_Unsubscribe_ - subscriber nodes can unsubscribe from any topics that they previously subscribed to.


_Publish_ - publisher nodes can create new events to notify relevant subscribers.


_Advertise_ - publisher nodes can create new topics for all nodes to use.


_Notify_ - the event manager tells all users when a new topic is created, and notifies 
subscribed nodes when an event is of a topic that they are subscribed to.