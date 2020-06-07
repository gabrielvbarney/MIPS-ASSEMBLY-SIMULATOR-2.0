main:

	#store parameter values into a0-a3
	add $sp, $0, $0
	and $a0, $0, $0
	and $a1, $0, $0
	and $a2, $0, $0

	addi $a0, $0, 30
	addi $a1, $0, 100
	addi $a2, $0, 20
    
	jal Circle	#head

	and $a0, $0, $0
	and $a1, $0, $0
	and $a2, $0, $0
	and $a3, $0, $0

	addi $a0, $0, 30
	addi $a1, $0, 80
	addi $a2, $0, 30
	addi $a3, $0, 30

	jal Line 	#body
	
    and $a0, $0, $0
	and $a1, $0, $0
	and $a2, $0, $0
	and $a3, $0, $0

	addi $a0, $0, 20
	addi $a1, $0, 1
	addi $a2, $0, 30
	addi $a3, $0, 30

	jal Line 	#left leg

	
    and $a0, $0, $0
	and $a1, $0, $0
	and $a2, $0, $0
	and $a3, $0, $0

	addi $a0, $0, 40
	addi $a1, $0, 1
	addi $a2, $0, 30
	addi $a3, $0, 30

	jal Line 	#right leg
	
    and $a0, $0, $0
	and $a1, $0, $0
	and $a2, $0, $0
	and $a3, $0, $0

	addi $a0, $0, 15
	addi $a1, $0, 60
	addi $a2, $0, 30
	addi $a3, $0, 50

	jal Line 	#left leg
	
    and $a0, $0, $0
	and $a1, $0, $0
	and $a2, $0, $0
	and $a3, $0, $0

	addi $a0, $0, 30
	addi $a1, $0, 50
	addi $a2, $0, 45
	addi $a3, $0, 60

	jal Line 	#right leg

	and $a0, $0, $0
	and $a1, $0, $0
	and $a2, $0, $0

	addi $a0, $0, 24
	addi $a1, $0, 105
	addi $a2, $0, 3

	jal Circle	#left eye

	and $a0, $0, $0
	and $a1, $0, $0
	and $a2, $0, $0

	addi $a0, $0, 36
	addi $a1, $0, 105
	addi $a2, $0, 3

	jal Circle	#right eye
	
    and $a0, $0, $0
	and $a1, $0, $0
	and $a2, $0, $0
	and $a3, $0, $0

	addi $a0, $0, 25
	addi $a1, $0, 90
	addi $a2, $0, 35
	addi $a3, $0, 90

	jal Line 	#mouth center
	
    and $a0, $0, $0
	and $a1, $0, $0
	and $a2, $0, $0
	and $a3, $0, $0

	addi $a0, $0, 25
	addi $a1, $0, 90
	addi $a2, $0, 20
	addi $a3, $0, 95

	jal Line 	#mouth left

	and $a1, $0, $0
	and $a2, $0, $0
	and $a3, $0, $0

	addi $a0, $0, 35
	addi $a1, $0, 90
	addi $a2, $0, 40
	addi $a3, $0, 95

	jal Line 	#mouth right

	j exit


Circle:

	and $t0, $0, $0
	and $t1, $0, $0
	and $t2, $0, $0
	and $t3, $0, $0
	and $t4, $0, $0
	and $t5, $0, $0
	and $t6, $0, $0
	and $t7, $0, $0

	addi $t0, $t0, 0	# x= 0
	add $t1, $t1, $a2	# y = r
	sll $t4, $t1, 1		# 2*r
	addi $t3, $0, 3	# 3
	sub $t3, $t3, $t4 	#g = 3-2*r
	sll $t4, $t1, 2		#4*r
	addi $t5, $0, 10	# 10
	sub $t5, $t5, $t4 	#diagonalInc = 10-4*r
	addi $t6, $0, 6		# rightInc = 6

	loop:

	add $t7, $a0, $t0	#xc+x
	sw $t7, 0($sp)
	addi $sp, $sp, 1
	add $t7, $a1, $t1	#yc+y
	sw $t7, 0($sp)
	addi $sp, $sp, 1

	add $t7, $a0, $t0	#xc+x
	sw $t7, 0($sp)
	addi $sp, $sp, 1
	sub $t7, $a1, $t1	#yc-y
	sw $t7, 0($sp)
	addi $sp, $sp, 1

	sub $t7, $a0, $t0	#xc- x
	sw $t7, 0($sp)
	addi $sp, $sp, 1
	add $t7, $a1, $t1	#yc+y
	sw $t7, 0($sp)
	addi $sp, $sp, 1



	sub $t7, $a0, $t0	#xc- x
	sw $t7, 0($sp)
	addi $sp, $sp, 1
	sub $t7, $a1, $t1	#yc-y
	sw $t7, 0($sp)
	addi $sp, $sp, 1

	add $t7, $a0, $t1	#xc+y
	sw $t7, 0($sp)
	addi $sp, $sp, 1
	add $t7, $a1, $t0	#yc+x
	sw $t7, 0($sp)
	addi $sp, $sp, 1


	add $t7, $a0, $t1	#xc+y
	sw $t7, 0($sp)
	addi $sp, $sp, 1
	sub $t7, $a1, $t0	#yc - x
	sw $t7, 0($sp)
	addi $sp, $sp, 1

	sub $t7, $a0, $t1	#xc - y
	sw $t7, 0($sp)
	addi $sp, $sp, 1
	add $t7, $a1, $t0	#yc + x
	sw $t7, 0($sp)
	addi $sp, $sp, 1

	sub $t7, $a0, $t1	#xc - y
	sw $t7, 0($sp)
	addi $sp, $sp, 1
	sub $t7, $a1, $t0	#yc - x
	sw $t7, 0($sp)
	addi $sp, $sp, 1

	slt $t4, $t3, $0		# is g < 0 ? → 1=true, 0 = false
	bne  $t4, $0, else
	add $t3, $t5, $t3
	addi $t5, $t5, 8
	addi $t1, $t1, -1
	j block

	else:

	add $t3, $t6, $t3
	addi $t5, $t5, 4

	block:

	addi $t6, $t6, 4
	addi $t0, $t0, 1

	slt $t4, $t1, $t0	#is y < x?
	beq $t4, $0, loop

	end:

	jr $ra



Line:
	# x0 = $a0, y0 = $a1, x1 = $a2, y1 = $a3

	and $t0, $0, $0
	and $t1, $0, $0
	and $t2, $0, $0
	and $t3, $0, $0
	and $t4, $0, $0
	and $t5, $0, $0
	and $t6, $0, $0
	and $t7, $0, $0
	and $s2, $0, $0
	and $s3, $0, $0
	and $s7, $0, $0 # s7 og

	addi $s7, $s7, 1 #s7 = 1

	sub $t0, $a3, $a1 # y vals
	sub $t1, $a2, $a0 # x vals

	add $t2, $0, $t0 # abs values
	add $t3, $0, $t1

	slt $t4, $t0, $0
	beq $t4, $0, abs1
	sub $t2, $0, $t0

	abs1:

	slt $t5, $t1, $0
	beq $t5, $0, abs2
	sub $t3, $0, $t1

	abs2:

	slt $t6, $t3, $t2
	beq $t6, $s7,  st1 #this is where the error is, store the 1
	addi $t7, $0, 0
	j stj

	st1:

	addi $t7, $0, 1	# $t7 = st

	stj:

	and $t0, $t0, $0
	and $t1, $t1, $0
	and $t2, $t2, $0
	and $t3, $t3, $0
	and $t4, $t4, $0
	and $t5, $t5, $0
	and $t6, $t6, $0
	# clears all temps, need better register choosing convention if have time

	bne $t7, $s7, st0		# if st == 1

	add $t0, $t0, $a0	# swap x0 (a0), y0 (a1)
	and $a0, $a0, $0
	add $a0, $a0, $a1
	and $a1, $a1,$0
	add $a1, $a1, $t0	

	add $t1, $t1, $a2	# swap x1 (a2), y1 (a3)
	and $a2,$a2, $0
	add $a2, $a2, $a3
	and $a3, $a3, $0
	add $a3, $a3, $t1

	and $t0, $t0, $0	# clear
	and $t1, $t1, $0

	st0:

	slt $t0, $a2, $a0
	beq $t0, $0, deltax	# if x1 > x0, skip to place where assigning deltax

	and $t0, $0, $0

	add $t0, $t0, $a0	# swap x0 (a0), x1 (a2)
	and $a0, $a0, $0
	add $a0, $a0, $a2
	and $a2, $a2, $0
	add $a2, $a2, $t0

	add $t1, $t1, $a1	# swap y0 (a1), y1 (a3)
	and $a1,$a1, $0
	add $a1, $a1, $a3
	and $a3, $a3, $0
	add $a3, $a3, $t1

	deltax:

	and $t0, $t0, $0	# clear
	and $t1, $t1, $0

	sub $t0, $a2, $a0	# deltax = t0

	sub $t1, $a3, $a1
	add $t2, $0, $t1		# abs value (deltay) = t2
	slt $t3, $t1, $0
	beq $t3, $0, abs3
	sub $t2, $0, $t1

	abs3:

	and $t3, $t3, $0	# t3 = error
	and $t4, $t4, $0	# clear y
	add $t4, $t4, $a1	# t4 = y0

	slt $t5, $a1, $a3
	beq $t5, $0, neg1 	# if y0 < y1, ystep = 1, else ystep = -1
	addi $t6, $t6, 1
	j ypos

	neg1:		# else

	addi $t6, $t6, -1	# t6 = 1 or t6 = -1

	ypos:

	and $s2, $s2, $0
	and $s3, $s3, $0    # s3 = x1 + 1
	and $t5, $t5, $0    # t5 = error



	add $s2, $s2, $a0
	addi $s3, $a2, 1

	looper:

	beq $s2, $s3, end2	# for x in range(x0, x1 + 1)
	bne $t7, $s7, else2	# if st != 0 then go to else

	sw $t4, 0($sp)		# plot(y, x)
	addi $sp, $sp, 1
	sw $s2, 0($sp)
	addi $sp, $sp, 1
	j afterelse

	else2:

	sw $s2, 0($sp)	# plot(x, y)
	addi $sp, $sp, 1
	sw $t4, 0($sp)
	addi $sp, $sp, 1

	afterelse:

	add $t5, $t5, $t2	# $t5 = error

	#add $t1, $t5, $t5

	sll $t1, $t5, 1	# $t1 = 2 * error

	slt $t3, $t1, $t0
	addi $s2, $s2, 1	# increment loop, can we do backwards?? !
	bne $t3, $0, looper
	add $t4, $t4, $t6	# goes into if
	sub $t5, $t5, $t0
	j looper

	end2:

	jr $ra

exit:
