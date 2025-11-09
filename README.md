# Porado

Porado is a simple programming language that compiles to native assembly.

## Variables

Variables in Porado have strict static typing. Variable declaration requires developers to specify the data type and variable identifier in the following format:

```java
var <identifier> as <data-type>;
```

Variables can be instantiated at declaration, or later in the code by calling its identifier:

```java
var myVariable1 as int = 12345;
var myVariable2 as float;
myVariable2 = 123.45;
```

### Fixed Variables

Variables can be set to immutable and read-only by adding the `fixed` keyword before its data type. A fixed variable will only keep the value it was instantiated with; you can declare a fixed variable and instantiate it afterwards.

```java
var PI as fixed float = 3.14159265359;
PI = 3 // Will throw an error

var goldenRatio as fixed float;
goldenRatio = 1.61803398875;
goldenRatio = 123 // You can only instantiate a fixed variable once.
```

### Arrays

An array can hold multiple variables of the same type. You can access specific element of an array using its array index. Arrays in Porado start at 0 like most programming languages, and array lengths are fixed. To declare an array, you add the `array` keyword after the data type:

```java
var myArray as int array;
```
<!--arr myArray as int;-->

An array in Porado uses the square braces `[ ]` to declare its elements:

```java
[1,2,3,4,5,6,7,8,9,0]; // Just an array of integers

var myFloatArray as float array = [1.2, 3.4, 5.6];
```