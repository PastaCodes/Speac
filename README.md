# Speac

### What is Speac?

Speac refers to a project started around october 2020 by me, PastaCodes.
It includes the specifications for a mostly functioning programming language
and a set of tools used to create and execute programs in this language.

### Why is Speac?

Apart from it being a learning opportunity for myself, I envision this language becoming a tool to help
people that are just starting to code. Do I think it is hard to learn to code without Speac? Not really...
In the past many languages have had the at least partial goal to implement a simple and user-friendly syntax,
but I feel like there's still a spot for my language. It aims to teach beginners the *mentality* that us programmers
use when we're writing code, without obligating them to study a complex syntax beforehand.

#### During the development of this project the following points have been crucial:
- The Speac syntax, as the name suggests, should follow the rules of the language we use to speak:
  the so called natural language. Of course an interpreter for *true* natural language would require some form
  of artificial intelligence, but with a couple of compromises something pretty close should be possible.
- This experiment aims to be an opportunity to practice and learn more about programming
  and for this reason I have decided to avoid investigating and researching
  how programming languages are usually made.
  This of course means that many inefficiencies and bad practises are likely to be present in the source code
  and that much of the terminology used is not accurate and out of place.
  
## The Speac syntax

Let's see an example of a Speac instruction.

```
draw < a > on < b
```

It might look confusing at first.
I'll try to explain it by comparing it to what it would look like in a C-like language:

```java
draw_on(a, b);
```

From this you should already start to understand the basic structure.  
Why is this type of syntax useful? Well, it allows us to add arguments amongst the function name,
getting a bit closer to the way we construct sentences in english.

---
Let's discuss the structure further.

All instructions are made up of argument tokens, function tokens and symbol tokens.  
In the example we've just seen a moment ago `draw` and `on` are function tokens, while `a` and `b` are argument tokens, and `<`, `>` and `<` are symbols.
Every instruction contains a function call, but the name of the function may be split into multiple tokens.
In the previous example the function name was 'draw on'.

Router characters (`<`, `>`) are used to separate function tokens from argument tokens.
They 'point' towards the function token and away from the argument token,
indicating that the arguments are passed to the function.

> Note: The spaces used in the example are not necessary.
> The following alternatives are also valid (but not suggested):
> 
> `draw<a>on<b`
> 
> `draw  <  a  >  on  <  b`
> 
> Also in case of a function consisting of multiple consecutive tokens (not interrupted by arguments)
> you can add additional spaces:
> 
> `draw on < b`
> 
> `draw  on < b`
> 
> But at least one must be present:
> 
> `drawon < b` ≠ `draw on < b`

Router characters are not needed at the beginning and end of the instruction because in these positions
they would not give additional information and would not help to separate tokens.

> Note: A right-pointing router character is to be added at the end of calls to variadic functions, although
> this changes nothing and is merely for readability purposes.
>
> | Normal Notation:          | Preferred Notation for Variadic Calls: |
> |---------------------------|----------------------------------------|
> | `list with < 1 >< 2 >< 3` | `list with <1><2><3>`                  |

Arguments may be variable names, strings, immediate numeric values or other instructions, nested inside parentheses.
For example:
```
create < "sum" > as < (9.0 > plus < 10)
say < sum
```
(Of course the output of this snippet would be 21.0)

Also don't panic because of the 'create as' function or because 'sum' is inside quotes, we'll discuss this later.

---

A function is also identified by the positions in which arguments are expected.

`draw on < b` ≠ `draw < b > on`

An instruction without router characters is interpreted as a function call without arguments.

This language **IS** case-sensitive.

## The structure of a Speac program

As I've mentioned earlier, all instructions must contain a function call.  
In most languages that's not the case, for example:
```java
int a = 5;
```
This is an instruction but it doesn't contain a function call.  
What would it look like in Speac?
```
create < "a" > as < 5
```
Oh yeah.  
From this you can learn a couple of things:
- Speac is a weakly typed language.
- Even the most basic instructions are replaced by built-in functions. 
  You might argue that programs written in such a language would tend to be long-winded and verbose,
  and that would be correct. But it's not necessarily a bad thing,
  as it more closely represents what the natural language is like, and that's the basic premise of this entire project.
- When an argument is a variable name, that variable *must* be defined, no exceptions, even at declaration.
  This means that when we're defining a variable we must use a string to indicate its name.
- Same thing applies to functions: prototypes must be enclosed in double quotes.

Example of a function definition:
```
define < "say < sentence > twice"
  repeat < 2 > times
    say < sentence
```