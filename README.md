superstartrek
=============

&gt;&gt;&gt;&gt;[Click to play](https://ggeorgovassilis.github.io/superstartrek/site/index.html)&lt;&lt;&lt;&lt;

Super Star Trek revived as an HTML 5 app.

A mobile friendly web remake of [Super Star Trek](https://en.wikipedia.org/wiki/Star_Trek_\(1971_video_game\)


![Screenshot 1](https://github.com/ggeorgovassilis/superstartrek/raw/gh-pages/images-for-README/screenshot1.png "Screenshot 1")

![Screenshot 2](https://github.com/ggeorgovassilis/superstartrek/raw/gh-pages/images-for-README/screenshot2.png "Screenshot 2")

## Developing

**Important note**: the main development branch is gh-pages

GWT devmode configuration for Eclipse: `-startupUrl index.html -war target/sst -noserver -style PRETTY -XmethodNameDisplayMode ONLY_METHOD_NAME superstartrek.sst-dev`

Build locally:
`mvn install`

## Software architecture

This chapter records random considerations regarding software architecture.

### Why GWT?

The first edition of SST was written with plain Javascript and jquery, but suffered from bugs, memory leaks, bad cross browser compatibility and bad performance. It didn't have unit- or integration tests and as the game logic became more complex the Javascript language features didn't help with design.

The [Google Web Toolkit](http://www.gwtproject.org/) is a Java-to-Javascript compiler which performs advanced code optimisations, abstracts browser differences away at compile- rather than run-time and grants all the benefits of a statically typed language (which the game logic greatly benefits from). Also, the main developer of this project is more familiar with the
Java ecosystem than the Javascript ecosystem.

### Why a PWA?

[Progressive Web Apps](https://en.wikipedia.org/wiki/Progressive_web_application) run on all major browser platforms and can be installed as native applications offline on mobile phones and desktop computers. They are written in HTML and Javascript and thus do not require platform-specific binary deployment packages. All this greatly improves user access to the game and reduces development and maintenance effort.

###  Why did superstartrek not stick to the original user interface?

The original user interface was conceived in the 70s and optimised for terminals with keyboards. The game falls clearly into
the strategy genre and thus is best played with either a mouse or a touch screen. Also, because it is 50 years later now and technology and user expectations have changed. The graphically, map-centred UI of superstartrek is more intuitive and easier to interact with on mobile devices.

### The Model-View-Presenter-Controller pattern

The [MVPC](https://blog.georgovassilis.com/2019/04/14/the-model-view-presenter-controller-pattern/) design pattern is better
suited for UI-driven applications than the MVC pattern. 

Views are thin, dumb wrappers around the client UI API; in this case the browser DOM. They are meant to be simple because, as having dependencies on the browser API, they can not be unit tested easily (without introducing another framework). Views don't try to abstract any domain concepts, instead they export underlying technology concepts to their public APIs. That is why views
in superstartrek often have methods like `addCssToCell(css, id)` which require the caller to be aware of styling semantics. Views register listeners to DOM events such as `onClick` and call presenter methods. Views implement one interaction element only, such as a widget or a screen. Views do not implement multiple screens or widgets. 

Presenters implement domain logic and deal with controllers and views, so they are aware of both view APIs and the underlying technology semantics and domain concepts. Since presenters don't rely on any technology APIs they can be easily unit tested when provided with mocked dependencies. Presenters implement a single domain activity such as the short range scanner or the context menu. Presenters expose callback methods for the view to invoke when a DOM event occurs.

Controllers take care of overarching concerns, mainly control transitions between presenters. superstartrek has two controllers, the `GameController` which starts the game and observes game-over conditions and the `PWA` controller which deals with technical topics such as version updates and caching.

### Events

Domain events extends the `Event` class and represent events that happen in the game such as a Klingon firing or the Enterprise consuming energy. Presenters register listeners and broadcast events with the `EventBus`. An event-based design
has the benefit of decoupling events from actions, so that the component which generates an event isn't concerned with who
reacts to it. Also event-based code is easier to unit test. The main drawback are unintended side effects when a component modifies, as a reaction to an event, shared data structures - other components accessing those data structures may change behaviour based on the change in the shared data structures which makes the event processing order important.  


