learn < "utils"

ask < "Please enter a value for the first side of the triangle:"
create < "first side" > as < (answer > as number)
ask < "Please enter a value for the second side of the triangle:"
create < "second side" > as < (answer > as number)
ask < "Please enter a value for the third side of the triangle:"
create < "third side" > as < (answer > as number)

say < "The perimeter of this triangle is " >< (sum of < (list with <first side><second side><third side>))