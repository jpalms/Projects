## CoPaDS Project 2
### Justin Palmer
### Jeffrey Taglic

------------------------

Implements a pub/sub system across docker nodes. 
Uses TCP connections to perform functions between publishers, subscribers, and the event manager.

#### Features

_Subscribe_ - subscriber nodes can subscribe to topics directly, or through keyword.


_Unsubscribe_ - subscriber nodes can unsubscribe from any topics that they previously subscribed to.


_Publish_ - publisher nodes can create new events to notify relevant subscribers.


_Advertise_ - publisher nodes can create new topics for all nodes to use.


_Notify_ - the event manager tells all users when a new topic is created, and notifies 
subscribed nodes when an event is of a topic that they are subscribed to.

#### Starting it Up

In order to see the full feature set, at least three different nodes are necessary ot start up.

1) Run an EventManager node. This must be done first, as this node is the Server.
2) Run a User node (first arguement the Server's IP), and create an account / sign in as a Publisher.
3) Run a User node (first arguement the Server's IP), and create an account / sign in as a Subscriber.

For the most part, the features come from interacting with the User CLI in the User nodes, but it verifies everything
through the EventManager node.