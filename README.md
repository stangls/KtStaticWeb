[![Build Status](https://travis-ci.org/stangls/KtStaticWeb.svg?branch=master)](https://travis-ci.org/stangls/KtStaticWeb)

# KtStaticWeb

A small web framework for writing typesafe server-side web applications in Kotlin.
Based on at least the following projects:
* [Vert.x](vertx.io)
* [kotlinx.html](https://github.com/Kotlin/kotlinx.html)
* [kotlinx.css](https://github.com/kotlinx/kotlinx.css)

# Getting started

## Add dependency
See [here](https://jitpack.io/#stangls/KtStaticWeb/v0.0.1) for more build systems and different versions.
```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
dependencies {
  compile 'com.github.stangls:KtStaticWeb:v0.0.1'
}
```

## Example

We define a minimal main entry point and present a view.

```
fun main(args: Array<String>) {
    minimalWebFramework {
        present("/",IndexView)
    }
}
```

The view just consists of a title saying "Hello world!"

```
object IndexView : IHtmlView<Unit, Unit> {
    override fun render(model: Unit, session: Unit): BODY.() -> Unit = {
        h1 { +"Hello world!" }
    }
}
```
