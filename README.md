What is reactive progamming?
****************************
	- New programming paradigm.
	- Asynchronous and non blocking
	- Data flow as an Event/Message Driven stream.
	- Functional Style Code.
	- Back pressure on Data Streams.
	

Existing Imperative Programming Rest API: Synchronous and blocked communication model
-----------------------------------------------------------------------------------
	- Call to JPARepo to get list of entries from DB
	- App {blocked and waiting} DB (during db call. App call, wait.., data returned)
		ex: List<Document> documents = docRepository.getAllDocuments();
		

Data flow as an event or message driven stream
-----------------------------------------------
	- one event or message for every result item from Data Source.
	- Data Sources:
		> DB
		> External Service
		> File etc..
	- One event or message for completion or error.
	- Nutshell Data communication or Data Flow in reactive programming:
		> OnNext(item) - Data stream events
		> OnComplete() -> Completion/Success event
		> OnError() -> Error Event


Back Pressure on Data Streams:
--------------------------------
> Datasource producing more data then we expected, so way of the app to let DB know to slows down till it catch up.
> Helps to build stable system using reactive programming.


What is a Reactive Stream?
----------------------------
> Specification or Rules for a reactive stream to follow.
> Created/Contributed by Pivotal, Netflix, LightBend and Twitter, etc..
> Specification have following four interfaces:
	- Publisher
		> It expects the subscriber instance
		> one interface 
			public interface Publisher<T>{
				public void subscribe(Subscriber<? super T> sub);
			}
		> Publishers are like: Represents the source of data
			- Database
			- External Service etc..
		
	- Subscriber
		> Four method under Subscriber interface
			OnSubscribe(Subscription sub), OnNext(), OnError(), OnComplete()
		
		
	- Subscription
		> Subscription interface, two method
			request(), cancel()
			
	- Processor
		> Interface is nothing but combination of both Subscriber and publisher
			public interface Processor<T,R> extends Subscriber<T>, Publisher<R>{
			}
			
Reference Link: https://github.com/reactive-streams/reactive-streams-jvm

			
			
What are the library implements the specification of Reactive Libraries:
*************************************************************************
As of now we have following:

(1) RxJava
(2) Reactor
(3) Flow class - JDK 9

Note: Using Reactor or Project Reactor
	> Built and maintain by Pivotal
	> Recommended Library to work with Spring Boot.
	

Project Reactor:
****************
> Different modules are available:
	- Reactor Core, Reactor Test, Extra, Netty, Adapter, Kafka, RabbitMQ etc..

> We are using following - which helps building non-blocking restful API.
	- Reactor Core, Reactor Test and Reactor Netty
	



------------------------------------------------------------------

Test Project with Dependencies:
********************************


(1) Reactive-Web: build reactive web application with Spring WebFlux and Netty
(2) Reactive-MongoDB: Provides asynchronous stream processing with non-blocking back pressure for MongoDB.
(3) Lombok: Java annotation library which helps to reduce boilerplate code.
(4) Embedded MongoDB: Provides a plateform neutral way for running MongoDB in unit tests. Like H2 in memory database.

Write JUNIT Test cases on Flux:
-------------------------------
	- It is not the traditional way, where assertion are written.
	- Use Reactor-Test Class to validate the input stream / Flux stream/ Publisher sequence:
		> StepVerifier:	
			- vertify onNext element value based on the input stream, Order/Sequence Sensetive.
			- It require .verifyComplete(), it act as subscribe, without that it want start the flow of
			 the element from the flux
			- In the Error scenario, verifyComplete() will not run, use expectError() and verify() to
			 start the flow.
			- In case of testing Error Message, expectErrorMessage(), but can not have both expectError
				and expectErrorMessage()
				
				
Factory Methods:
-------------------
Flux:
	- For List: Flux.fromIterable
	- For Array: Flux.fromArray
	- For Stream: Flux.fromStream

Mono:
	- Just of Empty: mono.justOrEmpty for null
	- Supplier: mono.fromSupplier
	

Functional Aspect:
-----------------
Using factory methods on Flux then
	- Filter
	- Predicates
	- map, flatMap etc..


Parallel with SubscribeOn: Transform
-------------------------------------

- Transform using functional aspects:
	> On Stream, list -> filter list, map each element 
	> On List or stream -> flatMap to flatten the Flux<List<String>>

- To parallel execution: Key is 
	> subscribeOn(parallel())

- To maintain the order in parallel
	> concatMap: no performance improvement of parallel excution
	> flatMapSequential(): better performance 



Combine the flux and mono, publishers:
---------------------------------------
Merge:
	- It is faster but not maintain the order from both the flux
	
Concat
	- It maintains the order 
	- It is consume first flux and then another flux in regular scenario
	
Zip:
	- It will allow customize the perform opration on both the flux.
	
	
ErroHanding in Flux and Mono:
-------------------------------
- OnErrorResume
	> Return the default flux - with values
	
- OnErrorReturn
	> Return fallback value
	
- OnErrorMap:
	> Allow to use Custom Exception
	> Allo to use Retry
	> Allow to use Retry BackOff - Retry after some interval (Note: it will not follow exact interval time, meaing 1 min plus and minus)
	
	
BackPressure:
--------------
- Multiple way to handle,
	> Using subscription.request(#element want to receive)

- Better way to create BaseSubscriber with "hookOnNext"	


Note: Project reactor is push and pull data model, meaning publsiher publish or push the data to subscriber, and also subscriber 
has the control of backpressure and cacel the flux.

HotAndCold Publisher:
----------------------
Cold Publisher: 
	> All the subscriber will start getting/emit the value from begining.

Hot Publisher: Hot Sequence
	> First subsciber emit from the begining but subsequent will not start emit from the beginning. Classic example stock ticker.
	> Use publish method from ConnectableFlux.
	
	

Spring Boot 2.0 Framework Layer:
#################################

A) Spring Boot 2, based on Reactor framework
B) Reactor Framework further support or built on both reactive and servlet stack:

	(1) Reactive Stack:	
		> Spring WebFlux is a non-blocking web framework built from the ground up to take advantage of multi-core, next 
		  generation processors and handle massive numbers of concurrent connections.
		> Require Netty or Servlet 3.1+ containers
		> Reactive Stream network adapters
		> Sprint Security Reactive
		> Spring WebFlux
		> Spring Data Reactive Repositories for:
			Mongo, Cassandra, Redis, Couchbase, R2DBC		
	
	
	(2) Servlet Stack: Traditional
		> Spring MVC is build on the servlet API and uses a synchronous blocking I/O architecture with one-request-per 
		 thread model
		> Traditional servlet container
		> Servlet API, Spring Security, Spring MVC
		> Spring Data Repositories (JDBC, JPA, NOSQL)
	


	

Spring Web Flux - Function Web Module:
---------------------------------------
- Use Functions to route the request and response.
- RouterFunctin and HandlerFunction
- Router Function:
	- Route the incoming request (like similar to @RequestMapping annotation - (URI Mapping) in traditional MVC)
	- Find out appropriate mapping and handler function handle all the operation

- Handler Function:
	- Handles the request and response
	- Similar to the body of the function of the mapped URI 
	- Two classe:
		> ServerRequest: Represents the HttpRequest
		> SeverResponse: Represents the HttpResponse.

Client <---> Server --> Router Function <---> HandlerFunction.



Spring WebFlux - Non blocking Request/Response Flow:
------------------------------------------------------

(1) Client <-> Bidirectional - Request/Response
(2) Netty (Non-blocking server, intern uses event loop)
	> Bidirectional - Client and Adapter
	> Widely used by compnies
(3) Reactive Stream Adapter (Reactor-Netty), Bidirectional - WebFilter and Server
	> Reactor Netty comes default with Spring webflux
	> If another server ebing used then appropriate reactive stream adapter need to be configured in gridle.build
	> More details are available in documentation
(4) WebFilter - Bidirectional - Handler and Adapter
	> Any filtering operation in non-blocking fashion
(5) WebHandler (Dispatcher Handler) - Bidirectional with WebFilter
	> and Unidirectional with Controller or Functional Web - Based on implementation of non-blocking API
	> Responsible calling for appropriate endpoint, decided based on the annotation of the classes
	
	


Client            Network       (Spring Flux Application, Embedded Netty (API))
---------------------------------------------------------------------------------------	
Non blocking        Invoke Endpoint
client			---------- Flux ----------->
				<-------- promise (flux) ---
				------- Request(Unbound)---->
				<-------- onNext (1) -------
				<-------- onNext (2) -------
				<-------- onNext (3) -------
				<-------- onComplete() -------


Note: All the events after the invoke endpoints, are taken care by the server


Netty: (Channel + Events)
*****************************
> Netty is an asynchronous event-driven network application framework for rapid development of maintainable
high performance protocol servers and clients.
> Built on top of Java
> Protocol supported by Netty are FTP, HTTP, SMTP, WebSocket and etc,,
> Being Asynchronous - Free us from the network blocking calls, Handles large number of connections.
> Event-driven, meaning
	- Client requesting for a new connection is treated as an event
	- Client Requestion for data is treated as an event
	- Client posting for data is treated as an event
	- Errors are treated as an event
	
> Netty - Channel:
	- Channel: represents the open connection between the client and server, like tunnel
		
		non-blockingclient  <---> 	Channel (Inbound Events, Outbound Events)  <---> 	Netty
	
	- Channel is created and is active, then client and server can interact
	- Inbound Event from client to server - like (requesting or posting data, etc)		 
	- Outbound Event from server to client - like (opening/closing connection, sending response to client)
	
> Netty - Event loop:
	- Loop that looks for the events and execute them
	- Associate/register with single dedicated thread
	- Will have (event queue + loop) - According to event (inbound or outbound) in the queue, one by one events
		 are being executed from the event queue.
	
	
> Channel Lifecycle:
	(1) Channel is created: like server accepted the client connection and created channel
	(2) Channel registered with eventloop:
		> Needed for the eventloop to forward the events to the right channel in future
		> Single event loop in reality can handle many channels / clients, 
		> So event loop should know which channel to send the response based on the event
	(3) Channel is active:
		> Client and Server are connected. Ready to send and Receive events
	(4) Channel is inactive:
		> Client and Server are not connected
	(5) Channel is unregistered:
		> Channel is unregistered from the eventloop.
		
		



Services and Projects:
########################
Non-blocking API creation two approachs:
	- RestController
	- Functional Web Module


(1) Non blocking API created using tradition RestContoller:
	
	> Create Integer List: Delay by second and retrun as flux.
	> Controller have two endpoints, with MediaType and without Media Type.
	> With Media type will display result as stream of flux appears to the browser by every second.
	> Withoout Media type, will wait for all elements arrived and then display on browser.
	
	Teting Approch:
		(1) Creating step verifier
		(2) Get direct list and verify the size - ExpectBodyList and compare size
		(3) Get direct List and compare with expected list - Run Assert Equals on both the list
		(4) Similar to the 3, but do direct comparision with consumeWith
		(5) Stream Flux: validate initial 3-4 values and thenCancel. it should be good.

(2) Non blocking API created using function web module:
	
	> Create sample handler class, which return the Mono<ServerResponse>, can return flux of Stream, or mono value
	> Create RouterConfig, can define routes and called method reference to 
		
(3) Create Client project to consume the created non blocking API
	
	> Client call using:
		Retrieve: Get the access to the response body directly
		Exchange: Get the response to the ClientResponse which includes status, body, etc..
		
		
(4) Handling Exception:
	RestController:
	Runtime Exception: @ExceptionHandler usage.
		
	Functional Exception Handling:
		> Create class FunctionalWebExceptionHandler which extend default AbstactErrorWebExcptionalHandler
		> Override super constructor by adding serverCodecConfigurere 
		> Implement required methods
		
		

Streaming Endpoints:
**********************
- In non-blocking API, as soon as onComplete() event is received, the flow ends, vs in streming endpoints it will be continuosly the scbsciber/client receive the data as publisher got new data.

MongoDB:
--------
- Tailable Cursor - Connections remains oper after all the results are retrieved.
- Capped Colletion - Collection of fixed-size mongoDB, preserve the insertion order
