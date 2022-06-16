# Android Redux

> 一个 Android 端的状态管理框架🙌🙌🙌

------------

[![](https://jitpack.io/v/ceneax/Redux.svg)](https://jitpack.io/#ceneax/Redux)

------------

### 什么是 Redux ？👀

> Redux对于JavaScript应用而言是一个可预测状态的容器。换言之，它是一个应用数据流框架，而不是传统的像underscore.js或者AngularJs那样的库或者框架。
Redux最主要是用作应用状态的管理。简言之，Redux用一个单独的常量状态树（对象）保存这一整个应用的状态，这个对象不能直接被改变。当一些数据变化了，一个新的对象就会被创建（使用actions和reducers）。

以上是来自 web前端 [Redux 官网](https://www.redux.org.cn/ "Redux 官网")的解释。

### Redux 框架思想的优点

在应用中使用Redux有如下好处：
1. 预测始终有一个准确的数据源，就是store, 对于如何将actions以及应用的其他部分和当前的状态同步可以做到绝不混乱。
2. 维护具备可预测结果的性质和严格的组织结构让代码更容易维护。
3. 组织对代码应该如何组织更加严苛，这使代码更加一致，对团队协作更加容易。
4. 测试编写可测试代码的首要准则就是编写可以仅做一件事并且独立的小函数。Redux的代码几乎全部都是这样的函数：短小、纯粹、分离。

### 关于本库 Android Redux

由于本人平时开发常用的技术栈是 **JavaScript** 、 **Flutter** 、 **Android**，所以在这常用的三个技术栈中通常会相互借鉴其中任何一个中的开发思想，以便更好地去写代码。

在使用 **Flutter** 开发应用的时候，经常使用的一个状态管理框架是阿里巴巴闲鱼团队开源的 **[Fish Redux](https://github.com/alibaba/fish-redux "Fish Redux")** ，所以本库就是借鉴了 **Fish Redux** 框架的思想，而 **Fish Redux** 又是借鉴了 Web前端 Redux 框架的思想。

##### Web前端 Redux 核心概念

- **actions**
  Actions就是事件，Actions传递来自这个应用（用户接口，内部事件比如API调用和表单提交）的数据给store。store只获取来自Actions的信息。内部Actions就是简单的具有一个type属性（通常是常量）的JavaScript对象，这个对象描述了action的类型以及传递给store的负载信息。
- **reducers**
  reducer就是获得这个应用的当前状态和事件然后返回一个新状态的函数。
- **store**
  Store对象保存应用的状态并提供一些帮助方法来存取状态，分发状态以及注册监听。全部state由一个store来表示。任何action通过reducer返回一个新的状态对象。这就使得Redux非常简单以及可预测。

##### Flutter 阿里巴巴 Fish Redux 核心概念

- **View**
  视图层，用于构建UI界面。
- **Effect**
  事件层，用于处理项目业务逻辑。
- **Action**
  事件类型，由 View 层的控件触发Action，用来执行 Effect 层对应该Action事件逻辑。
- **Reducer**
  用于状态变更，Effect 层执行完逻辑后，若需要刷新UI，则执行 Reducer 中相应函数，函数中会创建一个新的 State，并通知 View 层重绘UI。

##### Android Redux 核心概念

- **View**
  视图层，即 Activity、Fragment，构建UI界面
- **Effect**
  事件层，用于处理项目业务逻辑。
- **State**
  状态持有层，用来保存当前最新状态数据。
- **Reducer**
  状态管理层，Effect 层执行完逻辑后，若需要刷新UI，则执行 Reducer 中相应函数，函数中会创建一个新的 State，并通知 View 层重绘UI。

### Android Redux 架构图

![](https://pic.imgdb.cn/item/62ab6bf1094754312930b0bb.png)

这样数据就会永远在一个环形结构中单向流动，不能反向流动，遵循 MVI 思想中所倡导的原则。

## 使用文档

### 引入依赖

##### 第一步：
项目根目录的 **build.gradle** 文件中加入以下代码：

```Groovy
allprojects {
		repositories {
			...
			// 加入这行代码
			maven { url 'https://jitpack.io' }
		}
	}
```

##### 第二步：
在项目模块中的 **build.gradle** 文件中加入以下代码：

```Groovy
dependencies {
	...
	implementation 'com.github.ceneax:Redux:需要引入的版本号'
}
```

### 开始使用

先看一个最简单的Demo，**Activity 相关代码**：

```Kotlin
// Activity 部分代码，即 View 层
// 需要实现 IReduxView 接口，接口的第一个泛型参数是 State 类
// 第二个参数是 Effect 类，并且固定使用 ReduxView 委托类去委托 IReduxView 接口
class DemoActivity : BaseActivity<ActivityDemoBinding>(), IReduxView<DemoState, DemoEffect> by ReduxView() {
	/**
	* 该方法是BaseActivity中封装的，用于在这里绑定事件，和本框架无关
	*/
	override fun bindEvent() {
		binding.btIncrease.setOnClickListener {
			// 当 Activity 实现了 IReduxView 接口后，框架内部会持有一个 effect 变量，可直接访问 Effect 层的方法
			// 按钮被点击后，调用 Effect 层的方法去更新 State 中的 Counter 值，并刷新UI
			effect.increaseCounter()
		}
	}
	
	/**
	* Activity中覆写该方法，将所有的UI控件相关的赋值都写在这个方法中
	* 当 Effect 层需要改变 State 刷新UI的时候，会自动回调该方法
	*/
	override fun invalidate(state: DemoState) {
		// 一个TextView，显示当前计数结果
		binding.tvCounter.text = state.counter
	}
}
```

**Effect 层相关代码**：

```Kotlin
// Effect 层相关代码
// 需要继承 ReduxEffect 类，该类的泛型参数为 Reducer
// 当继承了 ReduxEffect 类之后，DemoEffect 可以访问一个叫做 stateManager 的变量
// 该变量可以执行 Reducer 层的相关方法，真正开始去更新 State 和 UI
class DemoEffect : ReduxEffect<DemoReducer>() {
	fun increaseCounter() {
		// 这里只是简单的演示状态更新，并没有在该方法中处理复杂逻辑
		// 先获取当前 State 中最新的 counter 属性值
		val newCounter = stateManager.state.counter
		// 然后调用 Reducer 层的方法，更新 State 值和UI界面
		// 新的值为当前的值累加1
		stateManager.updateCounter(newCounter + 1)
	}
}
```

**Reducer 层相关代码**：

```Kotlin
// 首先定义一个类型为 Kotlin Data Class 的 State 类，并且实现了 IReduxState 接口
// 用来表明该类是 State
// 该类中的每一个属性字段内容均对应 UI 界面中每一个要展示的控件所需要的值
// 并且每一个属性均为 val 类型，保证了一旦创建就不可变的特性，实现了单向数据流
data class DemoState(
	// 默认值为0
	val counter: Int = 0
) : IReduxState

// 该类固定继承 ReduxReducer，泛型参数传上面定义好的 State 数据类
class DemoReducer : ReduxReducer<DemoState>() {
	/**
	* 当 Effect 层调用该方法后，会执行 setState 并使用 Kotlin Data Class 数据类
	* 自带的 copy 方法去创建一个新的 State 类，然后内部通知 View 层刷新UI
	*/
	fun updateCounter(newCounter: Int) = setState {
		copy(counter = newCounter)
	}
}
```

到这里，一个简单的可运行的Demo就写好了，没有涉及复杂的操作。

**View** 层只持有了 **State** 和 **Effect**，又因为 **State** 中的属性是 **val** 类型，所以在 **View** 层中，不能直接更改 **State** 的值，只能调用 **Effect** 中的方法去执行相应逻辑然后生成新的 **State**；

**Effect** 层只持有了 **Reducer**，即 **StateManager** 状态管理层，在 **Effect** 中执行完业务逻辑后，若需要更新UI，直接调用 **Reducer** 层相关方法即可。该层不可以访问到 **View** 层，即和 **Activity**、**Fragment** 隔离；

**Reducer** 层只持有了保存 **State** 状态的实体类，该层仅用来创建新的 **State** 并通知 **View** 层刷新UI。

### 指定 State 中单个或多个属性变化监听

假如有个需求，需要在屏幕上显示当前时间戳，每秒自动刷新一次，如果按照上面的Demo，那么最终效果是下面这样：

```Kotlin
// Activity 部分代码
class DemoActivity : BaseActivity<ActivityDemoBinding>(), IReduxView<DemoState, DemoEffect> by ReduxView() {
	override fun bindEvent() {
		binding.btIncrease.setOnClickListener {
			effect.increaseCounter()
		}
	}
	
	override fun initData() {
		// 当 Activity 的 onCreate 执行完毕，自动触发循环获取时间戳的方法
		effect.startTimer()
	}
	
	override fun invalidate(state: DemoState) {
		binding.tvCounter.text = state.counter
		// 一个TextView，显示当前时间戳，每隔1秒刷新一次
		binding.tvTimeNow.text = state.time.toString()
	}
}


// Effect 层相关代码
class DemoEffect : ReduxEffect<DemoReducer>() {
	fun increaseCounter() {
		val newCounter = stateManager.state.counter
		stateManager.updateCounter(newCounter + 1)
	}
	
	fun startTimer() {
		// 启动一个定时器，每隔1秒执行一次，并将获取到的时间戳更新到 State 中
		timer(1000) {
			stateManager.updateTime(System.currentMillis)
		}
	}
}


// Reducer 层相关代码
data class DemoState(
	val counter: Int = 0,
	// 当前时间戳
	val time: Long = 0
) : IReduxState

class DemoReducer : ReduxReducer<DemoState>() {
	fun updateCounter(newCounter: Int) = setState {
		copy(counter = newCounter)
	}
	
	fun updateTime(nowTime: Long) = setState {
		// 通知 View 层刷新UI
		copy(time = nowTime)
	}
}
```

这时候应该会发现一个小瑕疵，就是每次 **Reducer** 中执行 **updateTime()** 方法去生成新的 **State** 的时候，**counter** 属性并没有变更，但是由于 **Activity** 中 **invalidate(state: DemoState)** 方法体里进行了两个控件的同时刷新，就会使 **tvTimeNow** 控件刷新的时候一并也给 **tvCounter** 去刷新了，这样就产生了无效刷新，造成了不必要的资源浪费。所以框架提供了监听某个或多个 **State** 属性的方法，改造后的 **Activity** 代码如下：

```Kotlin
// 改造后的 Activity 代码
class DemoActivity : BaseActivity<ActivityDemoBinding>(), IReduxView<DemoState, DemoEffect> by ReduxView() {
	override fun bindEvent() {
		binding.btIncrease.setOnClickListener {
			effect.increaseCounter()
		}
		
		// observe 是一个扩展函数，需要传入 KProperty 类型的参数
		// KProperty 是 Kotlin 中用来表示一个类中某个属性或字段的类型
		// 这里监听的即为 DemoState 数据类中定义的 time 时间戳属性
		// 该方法可传入多个值
		observe(DemoState::time) {
			// 一个TextView，显示当前时间戳，每隔1秒刷新一次
			binding.tvTimeNow.text = time.toString()
		}
	}
	
	override fun initData() {
		// 当 Activity 的 onCreate 执行完毕，自动触发循环获取时间戳的方法
		effect.startTimer()
	}
	
	override fun invalidate(state: DemoState) {
		binding.tvCounter.text = state.counter
	}
}
```

这样改造后，因为 **State** 中的 **time** 属性被单独监听了，所以当 **Reducer** 中只修改 **time** 属性值的话，那么每次 **Reducer** 中执行 **updateTime(nowTime: Long)** 去刷新UI都会只执行 **observe(DemoState::time)** 这个方法，而不会执行 **invalidate(state: DemoState)** 了，从而避免了无效刷新，浪费资源。

## 其它特性

### BeforeData

**BeforeData** 是一个能够自动获取前一个界面跳转时传递过来的 **Bundle**，并自动注入到 **Reducer** 层的一个功能。由注解来标记 **Reducer** 中哪些属性是 **BeforeData**。

示例：

```Kotlin
data class DemoState(
	// 默认值为0
	val counter: Int = 0
) : IReduxState

class DemoReducer : ReduxReducer<DemoState>() {
	// 该属性使用 @BD 注解来标记，当上一个 Activity 传递值的时候，会自动解析并注入到该变量中
	// 可以赋值一个默认值，所以当未在 Bundle 中找到和该属性名相匹配的参数的时候
	// 会保持当前默认值
	// 使用 val 修饰，则任何地方都不能去修改，保证了 BeforeData 的值的唯一性和可信任性
	@BD val content: String = "我是默认值"
	
	// 如果必须要去改 BeforeData 值，则可以使用下面这种方式，私有构造器 private set 保证了
	// 只有本类可以更改该属性值，Effect、View 层或其他类均不可更改该属性值
	@field:BD var canModifyValue: String = ""
	private set
	
	fun updateCounter(newCounter: Int) = setState {
		copy(counter = newCounter)
	}
}
```

上面展示了如何在 **Reducer** 层中去定义一个 **BeforeData** 属性，下面展示如何在 **Activity** 中传值：

```Kotlin
// 这是前一个 Activity 部分关键代码
class BeforeActivity : BaseActivity() {
  override fun bindEvent() {
    binding.btStartDemoActivity.setOnClickListener {
      // Android 系统提供了一个 bundleOf 扩展方法，用来快速创建 Bundle 类，其中
      // key 为 String 类型，value 为 Any 类型
      // 但是这里的 bundleOf 不是 Android 系统提供的扩展函数，而是该框架提供的一个
      // key 为 KProperty，value 为 Any 的扩展函数，这样的好处是，直接使用目标 
      // Reducer 层中已经定义好的 Kotlin 属性，当目标 Reducer 中 BeforeData
      // 相关属性名称发生变化了，那么这里由于找不到 DemoReducer 中 KProperty
      // 的引用，而使编译器报错，来提醒开发者要同步修改这里的代码。避免了界面跳转
      // 传值时上一个界面定义的 key 和 接收方定义的 key 不一致而导致获取不到值的问题
      val bundle = bundleOf(
        DemoReducer::content to "我是传递的值"
      )
      startActivity(Intent(this, DemoActivity::class.java).putExtras(bundle))
    }
  }
}
```

### 同步执行的 DialogFragment

文档后续补充

### 路由功能

该功能还在开发阶段，后续版本开放