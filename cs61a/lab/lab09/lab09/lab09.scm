;; Scheme ;;

; Q2
(define (cube x)
  'YOUR-CODE-HERE
  (* x x x)
)


; Q3
(define (over-or-under x y)
  'YOUR-CODE-HERE
  	(cond
  		((< x y) -1)
  		((= x y) 0)
  		(else 1)
  	)
)


; Q4
(define lst
  (list (list 1) 2 (cons 3 4) 5)
)
  

; Q5
(define (remove item lst)
  (cond
    ((null? lst) nil)
    ((= (car lst) item) (remove item (cdr lst)))
    (else (cons (car lst) (remove item (cdr lst))))
  )
)

;;; Tests

(remove 3 nil)
; expect ()
(remove 3 '(1 3 5))
; expect (1 5)
(remove 5 '(5 3 5 5 1 4 5 4))
; expect (3 1 4 4)


; Q6
(define (filter f lst)
  (cond
    ((null? lst) nil)
    ((f (car lst)) (cons (car lst) (filter f (cdr lst))))
    (else (filter f (cdr lst)))
  )
)


; Q7
(define (make-adder num)
  (lambda (x) (+ x num))
)


; Q8
(define (composed f g)
  (lambda (x) (f (g x)))
)

