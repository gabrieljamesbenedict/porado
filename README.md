# Porado
- [Porado](#porado)
  - [Introduction](#introduction)
  - [Comments](#comments)
    - [Single-line Comments](#single-line-comments)
    - [Multi-line Comments](#multi-line-comments)
  - [Variables](#variables)
    - [Data Types](#data-types)
    - [Strict Variables](#strict-variables)
    - [Fixed Variables](#fixed-variables)
  - [Arrays](#arrays)
    - [Multi-Dimensional Arrays](#multi-dimensional-arrays)
    - [Array and Array Element Modifiers](#array-and-array-element-modifiers)
  - [Operators](#operators)
    - [Arithmetic Operators](#arithmetic-operators)
      - [Unary Arithmetic Operators](#unary-arithmetic-operators)
    - [Assignment Operators](#assignment-operators)
    - [Comparison Operators](#comparison-operators)
    - [Logical Operators](#logical-operators)
    - [Order of Precedence](#order-of-precedence)
  - [Conditionals](#conditionals)
    - [If](#if)
    - [Else](#else)
    - [Else If](#else-if)
  - [Switch](#switch)
  - [Loops](#loops)
    - [While](#while)
    - [Do While](#do-while)
    - [Until](#until)
    - [Do Until](#do-until)
    - [For](#for)
    - [Repeat](#repeat)
    - [Break \& Continue](#break--continue)
      - [Break](#break)
      - [Continue](#continue)
  - [Functions](#functions)
    - [Recursion](#recursion)


## Introduction

Porado is a simple and fun programming language that takes the traditional syntax inspired from the C family of programming languages and blends it with natural English speech to achieve code that is both intuitive and effective.

## Comments

Comments are ignored by the compiler and are used to explain or temporarily remove code. 

### Single-line Comments

A single line comment can be made using two forward slashes `//`. The entire line starting from the `//` is ignored by the compiler.

```js
// This line is ignored!
num as int; // This part is ignored!
```

### Multi-line Comments

To comment out multiple lines, use `/* */`. Any text between these two symbols are commented out and ignored by the compiler.

```js
/*

This entire area is ignored!

*/

num as /* You can even comment in the middle of lines! */ int;
```

## Variables

Variables are used for containing data. In Porado, variables have explicit static typing, meaning you must clearly define the data type for each variable and it cannot change in runtime. Variable modifiers alter the normal behavior of variables. Multiple variable modifiers can be put into the same variable and combined in any order.

Variables are declared using this general pattern:

```js
<variable-identifier> as <modifiers...> <data-type>;
```

A value can be assigned to a variable during declaration or later by referencing its identifier using the `=` assignment operator using this patterns:

```js
<variable-identifier> as <modifiers...> <data-type> = <value>;

<variable-identifier> = <value>;
```


### Data Types

Porado has five basic data types that covers the majority of use cases in programming. Available data types include `int`, `float`, `char`, `string`, and `boolean`. A variable that is declared but not assigned a value uses its default value (determined by its data type) when its value is accessed during runtime.

| Data Type | Description | Default Value |
|:---------:|:------------|:-------------:|
| int | 32-bit signed integer value | 0
| float | 32-bit signed floating point value | 0.0 |
| char | 8-bit ASCII character | '' |
| string | UTF-8 encoded sequence of char values | "" |
| boolean | Logical truth value (true or false) | false |

```js
num as int;
name as string;
key as char;
price as float;
isDay as boolean;
```

### Strict Variables

A strict variable is a variable that doesn't use default values. When the value of a strict variable is accessed before it was assigned a value, a runtime error is occured.

To declare a variable as a strict variable, add the `strict` modifier keyword in the variable declaration:

```js
num as strict int;

print(num); // Runtime error
```

### Fixed Variables

A fixed variable is immutable and can only be assigned a value only once. Attempting to assign a new value to a fixed variable after it was already assigned a value will make a runtime error occur.

To declare a variable as a fixed variable, add the `fixed` modifier keyword in the variable declaration:

```js
num as fixed int;
num = 10;
num = 20; // Runtime error
```

## Arrays

Arrays are used to store multiple values of the same type. In Porado, arrays are immutable, meaning you can't change the array length after its declaration. An array must have a specified length at declaration.

Arrays are declared using this pattern:

```js
<array-identifier> as <modifiers...> array of <array-length> <element-modifiers...> <element-data-type>;
```

```js
nums as array of 10 int;
```

Square braces `[ ]` are used to denote lists for arrays. Each element in the list must be of the same type, and are separated by comma `,`. If an array is declared and initialized with a list of elements, the length of the array can be infered and omitted from the declaration.

```js
nums as array of int = [1,2,3,4,5];
```

An element of the array can be accessed using the array access operator `[ ]` which is appended to the left of an array identifier and contains an integer. It references a corresponding element where the integer is the index position of that element, allowing both read and write operations. An integer that is negative or greater than or equal to the array causes a runtime error. Indexes of array elements begin at 0.

```js
nums as array of int = [2,4,6,8,10];


nums[0] = 1; // Assign to 1
num2 = nums[4] // num2 == 10
num3 = nums[-1] // Runtime error
num4 = nums[10] // Runtime error
```

### Multi-Dimensional Arrays

Arrays can store any data, even other arrays. Storing arrays inside of arrays makes multi-dimensional arrays. All child arrays of a parent array must have the same length. Multi-dimensional arrays can be used to mimic tables with rows and columns.

To declare a multi-dimensional array, replace the data type to an array type:

```js
// num is an array of 5 arrays of 10 integers
// 50 total elements
nums as array of 5 array of 10 int;

// nums is an array of 5 arrays of 10 arrays of 15 integers.
// 750 total elements
nums as array of 5 array of 10 array of 15 int;
```

You can initialize the multi-dimensional array with an existing list to omit verbose code:

```js
nums as array of array of int = [
    [1,2,3],
    [4,5,6],
    [7,8,9]
];
```

Values in multi-dimensional arrays can be accessed, read, and wrote to using chained array access operators `[ ]` to refernce the element. The leftmost access operator references the top array and the rightmost access operator references the element.

```js
nums as array of array of int = [
    [1,2,3],
    [4,5,6],
    [7,8,9]
];

nums[0][0] = 10; // Assign to 10
num1 = nums[0][1] // nums1 == 2
num2 = nums[1][1] // nums5 == 5
num3 = nums[3][1] // Runtime error
```

### Array and Array Element Modifiers

Modifiers that are used for variables can also be applied for arrays and its elements.

The `strict` keyword when used on an array declaration turns the array into a strict array. A strict array will cause a runtime error when accessing any of its elements before all of its elements are explicitly initialized.

Using the `strict` modifier on its elements turns them into strict variables. Accessing an element before it is initialized will cause a runtime error.

```js
nums as strict array of 5 int;
print(nums[0]); // Runtime error
nums[0] = 0;
print(nums[0]) // Still runtime error; all elements need to initialized
```

```js
nums as array of 5 strict int;
print(nums[0]); // Runtime error
nums[0] = 0;
print(nums[0]) // No error
print(nums[1]); // Runtime error; element at index 1 is not yet initialized
```

The `fixed` keyword when used on an array declaration turns the array into a fixed array. A fixed array can be explicitly assigned a value only once. Assigning more than once makes a runtime error occur.

When set as a modifier for the array elements, the elements are become `fixed` variables and cannot be assigned a new value after it was already assigned one.

```js
nums as fixed array of 5 int = [1,2,3,4,5];
nums = [6,7,8,9,0] // Runtime error
nums[0] = 100 // No error: only the array itself is immutable
```

```js
nums as  array of 5 fixed int = [1,2,3,4,5];
nums[0] = 100 // Runtime Error
nums = [6,7,8,9,0] // No error: only the elements are immutable
```

## Operators

Operators are used to perform different operations on numerical values. Operators in Porado fall into these categories: **arithmetic**, **assignment**, **comparison**, and **logical**.

### Arithmetic Operators

Arithmetic operators perform computations between two numerical literals or expressions.

| Operator | Name | Description | Format |
|:--------:|:-----|:-----------:|:------:|
| + | Addition | Adds two values | A + B |
| - | Subtraction | Subtracts one value from the other | A - B |
| * | Multiplication | Mutliplies two values | A * B |
| / | Division | Divides one value by the other | A / B |
| % | Modulo | Returns the division remainder | A % B |

#### Unary Arithmetic Operators

Some arithmetic operators only has one operand.

| Operator | Name | Description | Format |
|:--------:|:-----|:-----------:|:------:|
| ++ | Increment | Add 1 to value | ++A or A++ |
| -- | Decrement | Remove 1 from value | --A or A-- |
| - | Negative Symbol | Inverts the sign of the value | -A |

**Note:** Increment and Decrement operators can be appended to either the left or right side of the operand. When on the left side, the pre-increment or pre-decrement operator performs its operation before the value is accessed. On the right side, the value is accessed first before the operand performs its operation. Increment and decremenet operators can only be used on variables, not literals or expressions.

### Assignment Operators

Assignment operations are used to assign variables with value.

| Operator | Name | Description | Format |
|:--------:|:-----|:-----------:|:------:|
| = | Normal Assignment | Assigns a value to a variable | A = B |
| += | Addition Assignment | Assigns a variable the sum of itself and another value | A += B |
| -= | Subtraction Assignment | Assigns a variable the difference of itself and another value | A -= B |
| *= | Multiplication Assignment | Assigns a variable the product of itself and another value | A *= B |
| /= | Division Assignment | Assigns a variable the quotient of itself and another value | A /= B |
| %= | Modulo Assigment | Assigns a variable the remainder of the division of itself and another value | A %= B |

### Comparison Operators

Comparison operators compare two numerical values and evaluates either `true` or `false`.

| Operator | Name | Description | Format |
|:--------:|:-----|:-----------:|:------:|
| == | Equals | Returns `true` if both values are numerically equal; otherwise returns `false` | A == B |
| != | Not Equals | Returns `false` if both values are numerically equal; otherwise returns `true` | A != B |
| > | Greater Than | Returns `true` if first value is numerically larger than second value; otherwise returns `false` | A > B |
| < | Less Than | Returns `true` if first value is numerically smaller than second value; otherwise returns `false` | A < B |\
| >= | Greater Than Or Equals | Returns `true` if first value is numerically larger than or equal to second value; otherwise returns `false` | A >= B |
| <= | Less Than Or Equals | Returns `true` if first value is numerically smaller than or equal to second value; otherwise returns `false` | A <= B |

### Logical Operators

Logical operators compare boolean values and evaluate to true or false.

| Operator | Name | Description | Format |
|:--------:|:-----|:-----------:|:------:|
| not | Logical NOT | Unary operator, inverses the truth value of a boolean variable or literal | not A |
| and | Logical AND | Returns `true` if both values are `true`; otherwise returns `false` | A and B |
| nand | Logical NAND | Returns `false` if both values are `true`; otherwise returns `true` | A and B |
| or | Logical OR | Returns `true` if at least one value is `true`; otherwise returns `false` | A or B |
| nor | Logical NOR | Returns `true` if both values are `false`; otherwise returns `false` | A nor B |
| xor | Logical XOR | Returns `true` only if values are different; otherwise returns `false` | A xor B |
| xnor | Logical XNOR | Returns `true` only if values are the same; otherwise returns `false` | A xnor B |

### Order of Precedence

When an expression contains two or more operations, certain operators are evaluated first before the others. Porado has an order of precedence for operators to determine which gets evaluated first to ensure the result is correct and accurate.

| Precedence | Operation | Note |
|:----------:|:---------:|:-----:|
| 1 | `( )` | Expression inside the parenthesis is evaluated first |
| 2 | `++`, `--`, `-` | This is the negative symbol, not subtraction |
| 3 | `*`, `/`, `%` | - |
| 4 | `+`, `-` | - |
| 5 | `>`, `<`, `>=`, `<=` | - |
| 6 | `==`, `!=` | - |
| 7 | `not` | - |
| 8 | `and`, `nand` | - |
| 9 | `or`, `nor` | - |
| 10 | `xor`, `xnor` | - |
| 11 | `=` | - |

## Conditionals

A condition is an expression that produces a boolean value (true or false) after evaluation. It can be a combination of different groupings, values, operators, and expressions.

```js
x == 10
a > 0 and a <= 100
(num + 10) * 5 > 100
(x > 3) and (y <= 10) and (z == 5)
```

### If

`If` statements can be used to execute some code if a condition is `true`.

An `if` statement can be declared using this format:

```js
if (<condition>) then {
    // Code if condition is true...
}
```

If the code for an `if` statement is only one line, the curly praces `{ }` may be omitted.

```js
if (<condition>) then /* Code if condition is true*/;
```

### Else

An `else` statement executes some code if the `if` statement condition is `false`.

An `else` statement can be declared using this format:

```js
if (<condition>) then {
    // Code if condition is true...
} else {
    // Code if condition is false...
}
```

```js
if (<condition>) then /* Code if condition is true*/; else /* Code if condition is false*/;

if (<condition>) then /* Code if condition is true*/;
else /* Code if condition is false*/;
```

### Else If

An `else-if` statement executes some code if the previous `if` or `else-if` statement conditions are `false` and its condition is `true`. 

An `else-if` statement can be declared using this format:

```js
if (<condition>) then {
    // Code if condition1 is true...
} else if (<condition>) then {
    // Code if condition2 is true...
}
```

Multiple `else-if` statements can be chained together.

```js
if (<condition>) then {
    // Code if condition1 is true...
} else if (<condition>) then {
    // Code if condition2 is true...
} else if (<condition>) then {
    // Code if condition3 is true...
} else if (<condition>) then {
    // Code if condition4 is true...
} else if (<condition>) then {
    // Code if condition5 is true...
} else if (<condition>) then {
    // Code if condition6 is true...
...
```

An `else` statement can be put after the last `else-if` statement and runs its code if the `if` and all `else-if` statement conditions are false.

```js
if (<condition>) then {
    // Code if condition1 is true...
} else if (<condition>) then {
    // Code if condition2 is true...
} else {
    // Code if condition1 and condition2 are false...
}
```
Conditions are evaluated one after the other, so even one of the next conditions are `true`, their code won't be executed if an earlier condition also evaluates to `true`.

```js
age as int = 18;

if (age < 13) then {
    print("Child");
} else if (age < 20) then {
    print("Teen");
} else {
    print("Adult");
}

```

## Switch

When there are too many possible decisions to make, creating a chain of `else-if` statements can get cumbersome and suboptimal. A `switch` statement solves this by declaring multiple `case` statements inside of its body. A `switch` statement takes an expression as input, and chooses only one `case` statement to execute. The `switch` compares the value of its expression against each case target using value equality (==). The first matching case is executed.

A `switch` statement can be declared using this format:

```js
switch (<expression>) {

    case(<target>): {
        // Code if target1 is equal to the expression...
    }

    case(<target>): {
        // Code if target2 is equal to the expression...
    }

    case(<target>): {
        // Code if target3 is equal to the expression...
    }

}
```

A special `case` statement called a `default` statement does not have a target value, but is only run when all other `case` statements did not match the expression.

```js
switch (<expression>) {

    case(<target>): {
        // Code if target1 is equal to the expression...
    }

    case(<target>): {
        // Code if target2 is equal to the expression...
    }

    default: {
        // Code if neither target1 or target2 is equal to the expression...
    }

}
```

If the case statement code only includes one line, the curly braces may be omitted.

```js
switch (<expression>) {

    case(<target>): /* Code if target1 is equal to the expression...*/;
    case(<target>): /* Code if target2 is equal to the expression...*/;
    case(<target>): /* Code if target3 is equal to the expression...*/;
    case(<target>): /* Code if target4 is equal to the expression...*/;

    default: /* Code if no case statements are executed... */;

}
```

```js
grade as char = 'B';

switch (grade) {

    case('A'): {
        print("Excellent!");
    }

    case('B'): {
        print("Good job!");
    }

    case('C'): {
        print("You passed.");
    }

    default: {
        print("Invalid grade.");
    }

}
```

## Loops

Loops are used to iterate over some code. Loops in Porado include `while`, `do-while`, `until`, `do-until`, `for`, and `repeat` loops.

### While

A `while` loop iterates over a code as long as its condition is `true`. In each iteration, the `while` loop first checks its condition. If the condition is `true`, the code is executed and the next iteration begins; otherwise the `while` loop is exited.

A `while` loop can be declared using the format:

```js
while (<condition>) then {
    // Code is executed while condition is true...
}
```

### Do While

A `do-while` loop iterates over a code as long as its condition is `true`. In each iteration, the `do-while` loop executes its code first, then checks its condition. If the condition is `true`, the next iteration begins; otherwise the `do-while` loop is exited. This allows the `do-while` loop to run at least once.

A `do-while` loop can be declared using the format:

```js
do {
    // Code is executed while condition is true...
} while (<condition>);
```

### Until

The `until` loop is the inverse of a `while` loop.

An `until` loop iterates over a code as long as its condition is `false`. In each iteration, the `until` loop first checks its condition. If the condition is `false`, the code is executed and the next iteration begins; otherwise the `until` loop is exited.

An `until` loop can be declared using the format:

```js
until (<condition>) then {
    // Code is executed until condition is true...
}
```

### Do Until

A `do-until` loop iterates over a code as long as its condition is `false`. In each iteration, the `do-until` loop executes its code first, then checks its condition. If the condition is `false`, the next iteration begins; otherwise the `do-until` loop is exited. This allows the `do-until` loop to run at least once.

A `do-until` loop can be declared using the format:

```js
do {
    // Code is executed until condition is true...
} until (<condition>);
```

### For

A `for` loop iterates over every element in an array. An iteration variable is used to access an element from the array, and it changes to the next after each iteration.

A `for` loop can be declared using the format:

```js
for (each <iteration-variable> in <iteration-array>) {
    // Code is executed for every element in the array...
}
```
The iteration variable is a direct reference instead of a copy of the element in an array, allowing it to be used to mutate the data inside the `for` loop.

```js
nums as int array of 30;

// Write
for (each num in nums) {
    num = 10;
}

// Read
for (each num in nums) {
    print(num);
}
```

### Repeat

A `repeat` loop iterates some code a specified number of times. The specified value must be an integer.

A `repeat` loop can be written using the format:

```js
repeat (<integer>) {
    // Code is iterated a number of times equal to the integer...
}
```

An iteration variable can optionally be added to keep track of iterations. To include an iteration variable, use the `with` keyword followed by the variable declaration. The iteration variable must be of type `int`.

A starting point can be defined by initializing the iteration variable to a number, or not initializing to use the default value of 0.

```js
repeat (<integer>) with <variable-declaration> {
    // Code is iterated a number of times equal to the integer...
}
```

```js
repeat (5) with i as int {
    print(i); // 0, 1, 2, 3, 4
}
```

```js
repeat (5) with i as int = 3 {
    print(i); // 3, 4, 5, 6, 7
}
```

### Break & Continue

The `break` and `continue` keywords can be used to control the flow of loops in code.

#### Break

The `break` keyword exits the loop immediately.

```js
num as int = 0;

while (true) then {
    if (num > 100) {
        break; // Exits while loop if num > 100...
    }

    num++;
}
```

#### Continue

The `continue` keyword skips to the next iteration immediately.

```js
nums as int = [1,2,3,4,5,6,7,8,9,10];

for (each num in nums) {
    if (num % 2 == 0) {
        continue; // Skips every even number...
    }

    print(num);
}
```

## Functions

A function is a named block of code that performs a specific task. It can accept input values (parameters) and optionally produce an output value (return type). Functions are only executed when explicitly called. A function can be declared with parameters to accept input arguments when it is called. Function parameter variables are declared locally inside the function scope.

A function with a `return` type evaluates into a value of that type when it is called. The `return` keyword followed by a value is used to exit the function and determines what value the function call evaluates to. A function declared with a `return` type must ensure that all possible control flow paths end with a `return` statement that provides a value of the declared type. A function with no return type can be ended early by using the `return` keyword by itself.

Functions can be called by using its identifier followed by parenthesis `( )` that includes zero or more arguments separated by comma. The amount of arguments must match the amount of parameters, and each argument must match the data type of its corresponding parameter.

A function can be declared using the format:

```js
<function-identifier> as function accepts (<input-parameters...>) returns <return-type> {
    // Code runs when function is called...
}
```
```js
add as function accepts (num1 as int, num2 as int) returns int {
    return num1 + num2;
}

num as int = add(10, 20); // num == 30
```

A function with no parameters or return type can ommit the `accepts` and `returns` part of the declaration. A function does not return any value if it has no return keyword. Trying to access a value of a function with no return type will occur a runtime error.


```js
num as int;

setNum as function accepts (num1 as int) {
    num = num1;
}

setNum(123); // num == 123
```

```js
num as int = 123;

getNum as function returns int {
    return num;
}

newNum as int = getNum(); // newNum === 123
```

```js
hello as function {
    print("Hello World!");
}

hello(); // Hello World!


num as int = hello(); // Runtime error...
```

### Recursion

Recursion occurs when a function calls itself directly or indirectly. It is often used for problems that can be broken down into smaller, similar subproblems.

```js
factorial as function accepts (num as int) returns int {
    if (num == 1) {
        return 1;
    }

    return num * factorial(num - 1);
}

num as int = factorial(5); // num == 120
```