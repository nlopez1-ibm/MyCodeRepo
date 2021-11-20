       IDENTIFICATION DIVISION.
       PROGRAM-ID.  CQQ1A121.
       ENVIRONMENT DIVISION.
       DATA DIVISION.
       WORKING-STORAGE SECTION.
       01  FILLER.
           03  FILLER                   PIC X(32)   VALUE
                 'AllFusion(R) Gen r7'.
       LINKAGE SECTION.
       01  IEF-RUNTIME-PARM1  PIC X(1).
       PROCEDURE DIVISION USING IEF-RUNTIME-PARM1.
       MAIN-0008716309.
           MOVE "I" TO IEF-RUNTIME-PARM1.
           CALL 'CQQ1A131' USING IEF-RUNTIME-PARM1
           CALL 'IQQ1A031'.      
           GOBACK.    
      *     
      *   +->   CQQ1A121_SERVER_TERMINATION       11/10/2006  10:39
      *   !       IMPORTS:
      *   !         Work View imp_dialect iqq1_component (Transient,
      *   !         Optional, Import only)
      *   !           dialect_cd
      *   !         Work View imp_error iqq1_component (Transient,
      *   !         Optional, Import only)
      *   !           severity_code
      *   !           rollback_indicator
      *   !           origin_servid
      *   !           context_string
      *   !           return_code
      *   +---
