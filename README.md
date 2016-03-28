# formula-compiler

This project hosts our super simple command line compiler. It's purpose is to make formula creation a simplier process

## Usage
`fcompile <input file> <output file>`

## file structure

Input files are composed of two parts, the header and the body. The header is where macros are defined. The body is where the core of the formula is written. Macros can be defined to allow for simple text substitution to simply long formulas. The goal is to approximate functional programming.

* The header is defined by `**header**`
* The body is defined by `**body**`
* A new macro is defined by `macro: <macro name>` Followed by text that is tabbed out. The syntax is similar to python. Hard tabs and soft tabs will both work. Macros can be alphanumeric and use underscores
* Comments are any line that starts with `#`
* To reference a macro in the body use `$$<macro name>`

###Example file
```
**header**

#are any of the child records marked as complete
macro: isAnyChildSelected
   contains("Complete", childconcatenate({link to child}.{child table}.{selected}))
   
#sum up the value of cost on all child fields
macro: sumOfChildCost
   sum({link to child}.{child table}.{cost})
   
**body**
# If any child is marked as selected, then return the total cost
# else return a cost of zero
if($$isAnyChildSelected, $$sumOfChildCost, 0)
```
