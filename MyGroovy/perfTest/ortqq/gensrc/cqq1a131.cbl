       IDENTIFICATION DIVISION.
       PROGRAM-ID.  CQQ1A131.
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
           MOVE 'X' TO IEF-RUNTIME-PARM1. 
           EXEC CICS SEND TEXT FROM(TXT) LENGTH(L) END-EXEC.     
           CALL 'CQQ1A130'
           GOBACK.
      *

      *     CALL 'TIRFTMTS' USING IEF-RUNTIME-PARM1
       
      *   +->   CQQ1A131_SERVER_INIT              10/20/2006  09:59
      *   !       EXPORTS:
      *   !         Work View exp_reference iqq1_server_data
      *   !         (Transient, Export only)
      *   !           server_date
      *   !           server_time
      *   !           reference_id
      *   !           server_timestamp
      *   !
      *   !     PROCEDURE STATEMENTS
      *   !
     1*   !  NOTE:
     1*   !  Amaç için açklamaya baknz
     1*   !
     2*   !  NOTE:
     2*   !  RELEASE HISTORY
     2*   !  01_00 23-02-1998 Yeni sürüm
     3*   !
     4*   !  SET exp_reference iqq1_server_data server_timestamp TO
     4*   !  CURRENT_TIMESTAMP
     5*   !  SET exp_reference iqq1_server_data server_date TO
     5*   !  datetimestamp(exp_reference iqq1_server_data
     5*   !  server_timestamp)
     6*   !  SET exp_reference iqq1_server_data server_time TO
     6*   !  timetimestamp(exp_reference iqq1_server_data
     6*   !  server_timestamp)
     7*   !  SET exp_reference iqq1_server_data reference_id TO
     7*   !  exp_reference iqq1_server_data server_timestamp
      *   +---
