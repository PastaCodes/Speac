define < "n > factorial"
	if < (n > equals < 0)
		return < 1
	else
		return < (n > times < ((n > minus < 1) > factorial))

say < (0 > factorial)
say < (1 > factorial)
say < (2 > factorial)

say < (5 > factorial)
say < (7 > factorial)

say < (11 > factorial)