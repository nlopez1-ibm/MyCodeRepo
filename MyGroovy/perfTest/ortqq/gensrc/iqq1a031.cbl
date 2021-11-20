       IDENTIFICATION DIVISION.
       PROGRAM-ID.  IQQ1A031.
       ENVIRONMENT DIVISION.
       DATA DIVISION.
       WORKING-STORAGE SECTION.
       01  FILLER.
           03  FILLER                   PIC X(32)   VALUE
                 'CA Gen r8'.
       LINKAGE SECTION.
       01  IEF-RUNTIME-PARM1  PIC X(1).
       PROCEDURE DIVISION USING IEF-RUNTIME-PARM1.
           MOVE 'N' TO IEF-RUNTIME-PARM1          
      *
           GOBACK.
      *
       PARA-0022020190-INIT-EXIT.
           EXIT.

      *   +->   IQQ1A031_DYNAMIC_STR_PREPARE_S    01/03/2019  14:07
      *   !       IMPORTS:
      *   !         Entity View imp iqq1_dynamic_string_detail
      *   !                     (Transient, Optional, Import only)
      *   !           resource_name
      *   !           sequence_num
      *   !           dialect_code
      *   !         Group View (15) imp_group_parameters
      *   !           Entity View imp_g_parameter
      *   !                       iqq1_dynamic_string_detail
      *   !                       (Transient, Optional, Import only)
      *   !             parameter_value
      *   +---
