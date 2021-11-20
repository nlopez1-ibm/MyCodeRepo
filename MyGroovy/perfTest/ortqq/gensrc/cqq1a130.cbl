       IDENTIFICATION DIVISION.
       PROGRAM-ID.  CQQ1A130.
       ENVIRONMENT DIVISION.
       DATA DIVISION.
       WORKING-STORAGE SECTION.
       01  FILLER.
           03  FILLER                   PIC X(32)   VALUE
                 'AllFusion(R) Gen r7'.
           03 TXT                       PIC X(1). 
           03 L                         PIC 9.
       LINKAGE SECTION.
       01  IEF-RUNTIME-PARM1  PIC X(1).
       PROCEDURE DIVISION USING IEF-RUNTIME-PARM1.
       MAIN-0008192021.
      *
           CALL 'CQQ1A131'   
           MOVE 'X' TO IEF-RUNTIME-PARM1.            
           GOBACK.
      *
