; Load this file into an interactive session with:
; python3 scheme -load quiz03.scm

; Filter (from lab) takes a predicate procedure f and a list s. It returns a
; new list containing only elements x in s for which (f x) is a true value.
(define (filter f s)
    (cond ((null? s) '())
          ((f (car s)) (cons (car s) (filter f (cdr s))))
          (else (filter f (cdr s))))
)

; All takes a predicate procedure f and a list s. It returns whether (f x) is
; a true value for every element x in s.
(define (all f s)
    (cond
      ((null? s) #t)
      ((not (f (car s))) #f)
      (else (all f (cdr s)))
    )
)

; Every takes a two-argument predicate g and a list s. It returns a new list
; containing only elements x in s for which (g x y) is true for every y in s.
(define (every g s)
  (define (helper g s b)
    (cond
      ((null? s) nil)
      ((all (lambda (x) (g (car s) x)) b) (cons (car s) (helper g (cdr s) b)))
      (else (helper g (cdr s) b))
    )
  )
  (helper g s s)
)

; Return a minimum card.
(define (min hand) (car (every <= hand)))

; Fimp returns the card played under the fimping strategy in Cucumber.
(define (fimp hand highest)
    (define (helper hand highest)
      (cond
        ((null? hand) nil)
        ((<= highest (car hand)) (cons (car hand) (helper (cdr hand) highest)))
        (else (helper (cdr hand) highest))
      )
    )
    (cond
      ((null? (helper hand highest)) (min hand))
      (else (min (helper hand highest)))
    )
)
    

; Legal returns pairs of (card . control) for all legal plays in Cucumber.
(define (legal hand highest)
  (define least (min hand))
  (define (result hand)
    (if (null? hand) nil (begin
        (define card (car hand))
        (cond
          ((= card least) (cons (cons card (>= (car hand) highest)) (result (cdr hand))))
          ((>= card highest) (cons (cons card #t) (result (cdr hand))))
          (else (result (cdr hand)))
        ))
    )
  )
  (result hand))


