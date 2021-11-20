/* rexx */
say 'testDyn exec'
/*trace a
*/

alrc=bpxwdyn("alloc fi(syslib) da(sys1.maclib) shr msg(2)")
If alrc <> 0 then Do
   Say 'Allocate for 'Dsname' failed'
   Select
   /* dynamic allocation error - positive or very negative */
   When alrc > 0 | alrc < -19999 then Do
       Numeric Digits 10
       If alrc > 0 then ,
         Dynrc = D2X(alrc,8)
       Else ,
         Dynrc = D2X(4294967296+alrc)
       Say 'Dynamic allocation failed ,RC='Left(Dynrc,4),
           'Reason='Right(Dynrc,4)
       End
   /* argument error */
   When alrc > -10000 then Do
        argix = 0 - alrc - 20
        Say argix
        Say 'Argument in error: 'Word(alarg, argix)
        End
   /* should not occur */
   When alrc = 0 then Say 'Logic error, unexpected RC=0'
   /* message error */
   Otherwise Do
        mrc = 0 - alrc - 10000
        Say 'Message routine IEFDB476 returned code 'mrc
        End
   End
   Exit 8
   End
