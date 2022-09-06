define < "utils : is : variable > is < utils : is : type"
	if < (not < ((type of < utils : is : type) > equals < "String"))
		raise error < Invalid Argument Type > on < "type"

	return < ((type of < utils : is : variable) > equals < utils : is : type)


define < "utils : doesn't equal : a > doesn't equal < utils : doesn't equal : b"
	return < (not < (utils : doesn't equal : a > equals < utils : doesn't equal : b))


define < "increment < utils : increment by : variable name > by < utils : increment by : amount"
	if < (not < ((type of < utils : increment by : variable name) > equals < "String"))
		raise error < Invalid Argument Type > on < "variable name"

	borrow < utils : increment by : variable name

	if < (not < ((type of < (value of < utils : increment by : variable name)) > equals < "Integer"))
		raise error < Invalid Referenced Type > on < "variable name"

	if < (not < ((type of < utils : increment by : amount) > equals < "Integer"))
		raise error < Invalid Argument Type > on < "amount"

	set < utils : increment by : variable name > to < ((value of < utils : increment by : variable name) > plus < utils : increment by : amount)


define < "increment < utils : increment : variable name"
	if < (not < ((type of < utils : increment : variable name) > equals < "String"))
		raise error < Invalid Argument Type > on < "variable name"

	borrow < utils : increment : variable name
	increment < utils : increment : variable name > by < 1


define < "repeat from < utils : repeat from to on : start > to < utils : repeat from to on : end > on < utils : repeat from to on : counter name >< utils : repeat from to on : code"
	if < (not < ((type of < utils : repeat from to on : start) > equals < "Integer"))
		raise error < Invalid Argument Type > on < "start"

	if < (not < ((type of < utils : repeat from to on : end) > equals < "Integer"))
		raise error < Invalid Argument Type > on < "end"

	if < (not < ((type of < utils : repeat from to on : counter name) > equals < "String"))
		raise error < Invalid Argument Type > on < "counter name"

	if < (not < ((type of < utils : repeat from to on : code) > equals < "Code Block"))
		raise error < Invalid Argument Type > on < "code"

	if < (utils : repeat from to on : end > is less than < utils : repeat from to on : start)
		raise error < Invalid Argument > on < "start, end"

	create < utils : repeat from to on : counter name > as < utils : repeat from to on : start
	repeat
		run < utils : repeat from to on : code > here
		if < ((value of < utils : repeat from to on : counter name) > equals < utils : repeat from to on : end)
			break < 2 > times
		increment < utils : repeat from to on : counter name


define < "repeat < utils : repeat times on : n > times on < utils : repeat times on : counter name >< utils : repeat times on : code"
	if < (not < ((type of < utils : repeat times on : n) > equals < "Integer"))
		raise error < Invalid Argument Type > on < "n"

	if < (not < ((type of < utils : repeat times on : counter name) > equals < "String"))
		raise error < Invalid Argument Type > on < "counter name"

	if < (not < ((type of < utils : repeat times on : code) > equals < "Code Block"))
		raise error < Invalid Argument Type > on < "code"

	if < (utils : repeat times on : n > is less than < 0)
		raise error < Invalid Argument > on < "n"

	repeat from < 0 > to < (utils : repeat times on : n > minus < 1) > on < utils : repeat times on : counter name >< utils : repeat times on : code


define < "repeat < utils : repeat times : n > times < utils : repeat times : code"
	if < (not < ((type of < utils : repeat times : n) > equals < "Integer"))
		raise error < Invalid Argument Type > on < "n"

	if < (not < ((type of < utils : repeat times : code) > equals < "Code Block"))
		raise error < Invalid Argument Type > on < "code"

	if < (utils : repeat times : n > is less than < 0)
		raise error < Invalid Argument > on < "n"

	repeat < utils : repeat times : n > times on < "utils : repeat times : unused counter" >< utils : repeat times : code


define < "repeat for each < utils : repeat for each in : item name > in < utils : repeat for each in : list >< utils : repeat for each in : code"
	if < (not < ((type of < utils : repeat for each in : item name) > equals < "String"))
		raise error < Invalid Argument Type > on < "item name"

	if < (not < ((type of < utils : repeat for each in : list) > equals < "List"))
		raise error < Invalid Argument Type > on < "list"

	if < (not < ((type of < utils : repeat for each in : code) > equals < "Code Block"))
		raise error < Invalid Argument Type > on < "code"

	repeat < (length of < utils : repeat for each in : list) > times on < "utils : repeat for each in : hidden counter"
		create < utils : repeat for each in : item name > as < (item < utils : repeat for each in : hidden counter > of < utils : repeat for each in : list)
		run < utils : repeat for each in : code > here


define < "repeat for each item in < utils : repeat for each item in : list >< utils : repeat for each item in : code"
	if < (not < ((type of < utils : repeat for each item in : list) > equals < "List"))
		raise error < Invalid Argument Type > on < "list"

	if < (not < ((type of < utils : repeat for each item in : code) > equals < "Code Block"))
		raise error < Invalid Argument Type > on < "code"

	repeat < (length of < utils : repeat for each item in : list) > times < utils : repeat for each item in : code


define < "sum of < utils : sum of : list"
	if < (not < ((type of < utils : sum of : list) > equals < "List"))
		raise error < Invalid Argument Type > on < "list"

	create < "utils : sum of : sum" > as < 0
	repeat < (length of < utils : sum of : list) > times on < "utils : sum of : hidden counter"
		increment < "utils : sum of : sum" > by < (item < utils : sum of : hidden counter > of < list)
	return < utils : sum of : sum


define < "square root of < utils : square root of : radicand"
	return < (utils : square root of : radicand > to the < .5)


define < "cube root of < utils : cube root of : radicand"
	return < (utils : cube root of : radicand > to the < (1. > over < 3.))