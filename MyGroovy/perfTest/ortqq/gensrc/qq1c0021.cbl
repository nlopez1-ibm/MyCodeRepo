      *COMPILE1: LANG(COBOL) CMP(DEFAULT) CICS(Y) DB2()
      *COMPILE2: MQS(N) DLL(N) IP(N) LDAP(N) EXCI(N) RENT(N)
      *----------------------------------------------------------------*
      *         I D E N T I F I C A T I O N   D I V I S I O N          *
      *----------------------------------------------------------------*
      ** Rebuild test njl 01
       IDENTIFICATION DIVISION.
       PROGRAM-ID.  QQ1C0021.
       ENVIRONMENT DIVISION.
       CONFIGURATION SECTION.
       DATA DIVISION.
       WORKING-STORAGE SECTION.
       77  MYIND1                      PIC S9(10) COMP-3.
      * COPY QB5ESEC1.

      ************************************************
      * USE CHANNELS AND CONTAINERS                  *
      ************************************************
      * 01  WS-CA.
      *     COPY QQ1C0021.
         
      * 01 IQQ1A031-ID                PIC X(08)      VALUE 'IQQ1A031'.       
       PROCEDURE DIVISION.      
       000000-CONTROL.
           
           CALL 'CQQ1A121'          
           CALL 'DQQM00A1'
           END-CALL      
           
           EXEC CICS ABEND
                     ABCODE('999')
           END-EXEC.
       
      *     CALL 'XX5CDLLY' USING WSC-PROG-NAME
      *     CALL 'XXSQLS01' USING
            GOBACK.
