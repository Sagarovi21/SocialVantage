/****************************************************************
 *                                                               *
 *   Copyright 2019 by Teradata Corporation.          *
 *                                                               *
 *   All Rights Reserved.                                        *
 *                                                               *
 *   TERADATA CONFIDENTIAL AND TRADE SECRET                      *
 *                                                               *
 ****************************************************************/

/******************************************************************************

FILE        -   fpfsocialvantagescoring.cpp

 *******************************************************************************/

/*  Function DDL

 CREATE FUNCTION TD_SYSFNLIB.SOCIALVANTAGESCORING ()
 RETURNS TABLE VARYING USING FUNCTION SOCIALVANTAGESCORING_CONTRACT
 LANGUAGE CPP
 SPECIFIC TD_SYSFNLIB.SOCIALVANTAGESCORING
 NO SQL
 DETERMINISTIC
 PARAMETER STYLE TD_INTERNAL_SQLTABLE2
 EXTERNAL LOCAL;

 */

#ifdef WIN32
#pragma warning(disable : 4996)

#ifdef max
#undef max
#endif

#define WIN32_LEAN_AND_MEAN
#endif

extern "C" {
#include <sys/ntos/segget.h>
#include <sys/ntos/segdrop.h>
#include <fpf/fpfbase.h>
#include <sys/sysdbstypes.h>
#include <fnc/fnctypes.h>
#include <fpf/fpftblproto.h>
#include <fnc/fnctypes.h>
#include <isf/isfstrmove_proc.h>
}
#include <evl/evludttypes.h>
#include <str/str2up_proc.h>
#include <str/strgetlenw_proc.h>
#include <isf/isfcalc.h>
#include <uci/uciwrapper.h>
#include <uio/uiolobfileinputstream.h>
#include <uio/uiolobfileoutputstream.h>
#include <uio/uiomemoryoutputstream.h>
#include <sql/sqltypes_td.h>
#include <dbs/dbstypes.h>
#include <dbs/dbsccb.h>
#include <awt/awttypes.h>
#include <awt/awtgtthd_proc.h>
#include <err/errfnc.h>
#include <fpf/fpftypes.h>
#include <str/strprocs_proc.h>
#include <fpf/fpftblopastlib.h>
#include <fpf/fpfutils.h>

#include <algorithm>
#include <cstdlib>
#include <map>
#include <stdexcept>
#include <string>

#define FPPREDMAXVARCHAR 64000

#define IN_PRODUCT          1
#define IN_FEATURE          2
#define IN_COMMENT          3
#define IN_SENTIMENTSTRNGTH 6

#define OUT_PRODUCT         0
#define OUT_FEATURE         1
#define OUT_SCORE           2
#define OUT_PRODUCT_SCORE   3

typedef struct InputInfo_t {
   fpfcolinfo_t *iCols;
   FPF_TblOpHandle_t *Handle;
   int is_eof;
} InputInfo_t;


size_t strlenw_sv(const tdwchar* s1)
{
   register int    rslt = 0;
   while(*s1++ != (tdwchar)0)
      rslt++;
   return rslt;
}

/* this function replace given character(item) by '\0',
   if it is part of delimiter(delim) characters*/
int is_present_unicode_sv(tdwchar item, tdwchar *delim)
{

   ccbint32_t i;
   for(i=0; delim[i]!='\0'; i++)
   {
      if(delim[i]==item)
         return 1;
   }
   return 0;
}

/* Tab space, new line feed, Carriage Return...etc
 also considered as space*/
ccbint32_t isspace_unicode_sv(tdwchar chr)
{
   return chr==' '|| chr=='\t'|| chr=='\n'|| chr=='\v'|| chr=='\f'|| chr=='\r';
}


ccbint8_t isEmptyToken_sv(const tdwchar * token)
{
   int len = 0, i=0;

   len=strlenw_sv(token);
   for(i=0; i<len; i++)
   {
      if(!isspace_unicode_sv(((tdwchar*)token)[i]))
         return 0;
   }
   return 1;
}

/*this function spits sentence into words with supplied delimiter*/
Local tdwchar ** str_split_unicode_sv(tdwchar * str, tdwchar * delim, ccbint32_t* size)
{
   if(!str||!delim||str[0]=='\0')
      return NULL;
   int token_count=0, is_token=0;
   int indicator=0;

   while(str[indicator]!='\0')
   {
      /*If current charecter is present in the supplied delimiter charecters
        replace it with '\0' and search contineous characters for the same*/
      if(is_present_unicode_sv(str[indicator], delim))
      {
         str[indicator++]='\0';
         while(is_present_unicode_sv(str[indicator], delim))
         {
            str[indicator]='\0';
            indicator++;
         }
         is_token=0;
      }
      else
      {
         /*being is_token=0 and control is in this block indicates
             it is the first occurence of non delimiter charecter, nothing
              but a starting of a token, hence increase token_count by one */
         if(!is_token)
            ++token_count;
         ++is_token;
         ++indicator;
      }
   }
   /* for addressing tokens, based on the size of tokens(token_count)*/
   tdwchar** tokens=(tdwchar**)FNC_malloc(sizeof(tdwchar*)*token_count);
   int token_counter=0, token_itr=token_count;
   indicator=0;
   /* this loop iterates through the sentence based on the token count(token_itr)
    and stores each token in the above allocated space*/
   while(token_itr!=0)
   {
      if(str[indicator]=='\0')
      {
         while(str[indicator]=='\0')
         {
            indicator++;
         }
      }
      else
      {

         if(isEmptyToken_sv(&str[indicator]))
         {
            --token_itr;
            --token_count;
            indicator=indicator+strlenw_sv(&str[indicator]);
         }
         else
         {
            tokens[token_counter++]=&str[indicator];
            --token_itr;
            while(str[indicator]!='\0')
            {
               indicator++;
            }
         }

      }
   }
   *size=token_count;
   return tokens;
}

Local ccbint32_t
strindexwlong_sv(const tdwchar* Pattern,
      const tdwchar* Source,
      ccbint32_t TheIndex)
{
   ccbint32_t resultlen;
   ccbuint32_t Length, PatternLen, Index, Position;
   
   /*
    ** Initialize the result and other counters.
    */
   resultlen = 0;
   Index     = 0;
   Position  = 0;

   /*
    ** Get the pattern length
    */
   PatternLen = (ccbuint32_t) strlenw_sv(Pattern);

   if (PatternLen > 0)
   {
      /*
       ** Look for matching pattern in the source string.
       ** The length of the pattern should also match.
       */

      Length = (ccbuint32_t) (strlenw_sv(Source) - TheIndex + 1);
      while (Length >= PatternLen)
      {
         if (Source[TheIndex - 1] == Pattern[0])
         {
            Index    = TheIndex + 1;
            Position = 2;

            while (Position <= PatternLen)
            {
               if (Source[Index - 1] != Pattern[Position - 1])
               {
                  break;
               }
               else
               {
                  Index++;
                  Position++;
               }
            } /* end - while */

            if (Position > PatternLen)
               return TheIndex;
         } /* end - if (Source[TheIndex - 1] == Pattern[0]) */

         TheIndex++;
         Length--;

      } /* end - while */
   } /* end - if (PatternLen>0) */
   return resultlen;
} /* end - strindexwlong() */

Local void addColumn(fpfcolinfo_t *outputcols, int index, EvlRepresent_t &repr,
      const char *colname) {
   ISFNAMECOPYSTR2FULL(outputcols->coldefs[index].column, colname);
   memcpy((void *) &(outputcols->coldefs[index].repr), (void *) &repr,
         sizeof(EvlRepresent_t));
}

void socialvantagescoring_contract(MgcTableType *udfType, int *numParams,
      int * numResultParams, evlstackentry_t *stack,
      void *scalarAggrReturnValue, FldSizeType *scalarAggrReturnLength,
      int *scalarAggrReturnIndicator, ccbuint8_t * aggrPhase,
      FNC_Context_t * fnc_ctx, void * auxInfo, fpfresult_t *fpfresult) {

   sysreturn_t  result  = OK;
   sysreturn_t  errcode = OK;
   fpfcolinfo_t *iCols  = NILPTR, *oCols = NILPTR;
   int          incount = 1, outcount = 1;
   int          contCtxLen = 0;
   int          oColsCount = 0;
   std::string  errMsg;
   EvlRepresent_t repInt_id, repVarchar, repReal;

   const tdwchar prod_s[7]           = { 'p', 'r', 'o', 'd', 'u', 'c', 't' };
   const tdwchar feature_s[7]        = { 'f', 'e', 'a', 't', 'u', 'r', 'e' };
   const tdwchar score_s[5]          = { 's', 'c', 'o', 'r', 'e' };
   const tdwchar product_score_s[13] = { 'p', 'r', 'o', 'd', 'u', 'c', 't',
         '_', 's', 'c', 'o', 'r', 'e' };

   fpfStringWrap prod((const BYTE*) prod_s, 14, UNICODE_CT);
   fpfStringWrap feature((const BYTE*) feature_s, 32, UNICODE_CT);
   fpfStringWrap score((const BYTE*) score_s, 10, UNICODE_CT);
   fpfStringWrap product_score((const BYTE*) product_score_s, 26, UNICODE_CT);

   try {
      // Get the input tables(input and forest) columns definition
      FPF_TblOpGetStreamCount(&incount, &outcount);
      if (incount != 1) {
         throw FpfException("Only one input required");
      }

      for (int i = 0; i < incount; ++i) {
         iCols = (fpfcolinfo_t *) FPF_TblOpGetColDef(i, ISINPUT, &result);
         if (result != OK) {
            throw FpfException("FPF_TblOpGetColDef failed.");
         }
      }

      oColsCount = 4;
      contCtxLen = oColsCount * sizeof(fpfcoldef_t);
      segget(&result, (void **) &oCols, contCtxLen, SEGLOCKWRITE);
      if (result != OK) {
         throw FpfException("segget failed getting memory for output columns");
      }
      memset(oCols, 0, contCtxLen);
      SeggetPtr oColsPtr(oCols);
      oCols->numcolumns = oColsCount;

      /* Copy Output columns indexes. */

      int temp_oindex = 0;

      /* Add 4 new output columns */
      // 'product'
      INIT_EVLVARCHAR(repVarchar, FPPREDMAXVARCHAR, EVLUNICODE, EVLUNKNOWNCOLL);
      addColumn(oCols, temp_oindex++, repVarchar,
            prod.getPrintableString().c_str());
      // 'feature'
      INIT_EVLVARCHAR(repVarchar, FPPREDMAXVARCHAR, EVLUNICODE, EVLUNKNOWNCOLL);
      addColumn(oCols, temp_oindex++, repVarchar,
            feature.getPrintableString().c_str());
      // 'score'
      INIT_DFLTEVLREAL(repReal);
      addColumn(oCols, temp_oindex++, repReal,
            score.getPrintableString().c_str());

      // 'product_score'
      INIT_DFLTEVLREAL(repReal);
      addColumn(oCols, temp_oindex++, repReal,
            product_score.getPrintableString().c_str());

      //Set oCols as output column definition for AMP
      FPF_TblOpSetOutputColDefInternal(0, oCols, &result);
      if (result != OK) {
         throw FpfException("Failed to set output columns");
      }

      //  Return the number of output columns
      if (*scalarAggrReturnLength != sizeof(ccbint32_t)) {
         throw FpfException("contract: return length mismatch");
      }
      ccbint32_t* retval = (ccbint32_t *) scalarAggrReturnValue;
      *retval = (ccbint32_t) oCols->numcolumns;

   } catch (FpfException &fpfE) {
      errMsg = std::string("socialvantagescoring: ") + fpfE.msg;
      FPF_ERROR_HANDLERX(fpfresult, ERRAMPFPFUNCFAIL, errMsg.c_str());
   } catch (std::exception &e) {
      errMsg = std::string("socialvantagescoring: ") + e.what();
      FPF_ERROR_HANDLERX(fpfresult, ERRAMPFPFUNCFAIL, errMsg.c_str());
   } catch (...) {
      errMsg = std::string("socialvantagescoring: ") + "Unknown error.";
      FPF_ERROR_HANDLERX(fpfresult, ERRAMPFPFUNCFAIL, errMsg.c_str());
   }
} // socialvantagescoring_contract() ENDS


void socialvantagescoring() {

   fpfresult_t fpfresult;
   AwtTskGblPtrType AwtTskGbl;
   FNCTblCtx_t *tblCtxPtr = NILPTR;
   FNC_TblOpCtx_t *tblOpCtxPtr = NILPTR;
   sysreturn_t retcode;

   ccbint32_t incount;
   ccbint32_t outcount;
   InputInfo_t *icolinfo = NILPTR;
   std::string errMsg;
   FPF_TblOpHandle_t *handle, *outHandle;
   int numOfInputCols = 0;
   fpfStringWrap positive_score((const byte*)"positive score:", 15, LATIN_CT);
   positive_score.convert2Unicode();
   fpfStringWrap negative_score((const byte*)"negative score:", 15, LATIN_CT);
   negative_score.convert2Unicode();
   std::map<fpfStringWrap, std::pair<double, int> > featureMap; // <featureName, <sumScore, count> >
   std::map<fpfStringWrap, std::pair<double, int> >::iterator itr;
   bool isFirst = true;
   fpfStringWrap prodNm;
   double totalScoreAvg = 0;
   int totCommentCount = 0;
   tdwchar **fearuresList = NILPTR;

   try {

      /* Get the input column definitions */
      icolinfo = (InputInfo_t *) alloca(sizeof(InputInfo_t) );
      icolinfo->iCols
      = (fpfcolinfo_t*) FPF_TblOpGetColDef(0, ISINPUT, &retcode);
      if (retcode != OK) {
         throw FpfException("GetColDef has failed");
      }

      icolinfo->Handle = (FPF_TblOpHandle_t *) FPF_TblOpOpen(0, 'r', 0, NULL);
      numOfInputCols = icolinfo->iCols->numcolumns;

      /* while(read)
       *  {
       *    tokenize features
       *    search features in comment
       *    write avg(feature score)
       *  }
       */

      //Reading input table and storing column values temporarily.
      while (TBLOP_SUCCESS == FPF_TblOpRead(icolinfo->Handle, NULL)) {

         handle = icolinfo->Handle;
         if (!(handle->row->indicator[IN_SENTIMENTSTRNGTH] &&
               handle->row->lengths[IN_SENTIMENTSTRNGTH] > 0 &&
               handle->row->indicator[IN_FEATURE] &&
               handle->row->indicator[IN_COMMENT])) {
            continue;
         }

         if (isFirst) {
            prodNm = fpfStringWrap((byte*) handle->row->columnptr[IN_PRODUCT],
                  handle->row->lengths[IN_PRODUCT],
                  evlgetchartype(icolinfo->iCols->coldefs[IN_PRODUCT].repr));
            prodNm.convert2Unicode();

            /* Tokenize features */
            tdwchar featureStrSavePtr[20000] = { 0 };
            memcpy((void*)featureStrSavePtr,
                  handle->row->columnptr[IN_FEATURE],
                  handle->row->lengths[IN_FEATURE]);

            fearuresList = NILPTR;
            tdwchar delm_reset[6] = {',', '\'', ':', '[', ']'};
            int num_words = 0;
            fearuresList = str_split_unicode_sv(
                  featureStrSavePtr, delm_reset, &num_words);

            for(int i = 0; i < num_words; ++i) {
               fpfStringWrap featureToken((byte*)fearuresList[i],
                     strlenw_sv(fearuresList[i])*sizeof(tdwchar), 
                       evlgetchartype(icolinfo->iCols->coldefs[IN_FEATURE].repr));
               featureToken.trimSpaces(true, true);
               featureToken.convert2Unicode();
               featureToken.toUpper();
               featureMap.insert(std::make_pair(
                     featureToken, std::make_pair(0, 0)));
            }

            isFirst = false;
         }

         /* Search features in comment */

         //Fetch the +ve and -ve score from out sentiment
         fpfStringWrap outSentimentStr(
               handle->row->columnptr[IN_SENTIMENTSTRNGTH],
               handle->row->lengths[IN_SENTIMENTSTRNGTH],
               evlgetchartype(
                     icolinfo->iCols->coldefs[IN_SENTIMENTSTRNGTH].repr));
         outSentimentStr.convert2Unicode();

         ccbint32_t pos1 = strindexwlong_sv(
               (const tdwchar*)positive_score.getValue(),
               (const tdwchar*)outSentimentStr.getValue(), 1) - 1;
         ccbint32_t pos2 = strindexwlong_sv(
               (const tdwchar*)negative_score.getValue(),
               (const tdwchar*)outSentimentStr.getValue(), 1) - 1;

         fpfStringWrap posStr_temp;
         outSentimentStr.substr(
               pos1 + positive_score.getLength(), pos2 - 1, posStr_temp);
         int posScore = posStr_temp.str2i(NILPTR);

         fpfStringWrap negStr_temp;
         outSentimentStr.substr(pos2 + negative_score.getLength(),
               outSentimentStr.getLength() - 1, negStr_temp);
         int negScore = negStr_temp.str2i(NILPTR);

         fpfStringWrap cmnt(handle->row->columnptr[IN_COMMENT],
               handle->row->lengths[IN_COMMENT],
               evlgetchartype(icolinfo->iCols->coldefs[IN_COMMENT].repr));
         cmnt.convert2Unicode();
         cmnt.toUpper();

         totalScoreAvg += (posScore + negScore);
         ++totCommentCount;

         for (itr = featureMap.begin(); itr != featureMap.end(); ++itr) {

            ccbint32_t found = strindexwlong_sv(
                  (const tdwchar*)itr->first.getValue(),
                  (const tdwchar*)cmnt.getValue(), 1);

            if (found != 0) {
               itr->second.first = posScore + negScore;
               itr->second.second = itr->second.second + 1;
            }
         }
      }

      if (totCommentCount > 0) {
         totalScoreAvg = totalScoreAvg / totCommentCount;
      }

      //Opening output stream for writing
      outHandle = (FPF_TblOpHandle_t*) FPF_TblOpOpen(0, 'w', 0, NULL);

      for (itr = featureMap.begin(); itr != featureMap.end(); ++itr) {

         /*if (itr->second.second == 0) {
            continue;
         }*/

         outHandle->row->columnptr[OUT_PRODUCT]      =
               const_cast<byte*> (prodNm.getValue());
         outHandle->row->lengths[OUT_PRODUCT]        = prodNm.getByteLength();
         outHandle->row->indicator[OUT_PRODUCT]      = true;

         outHandle->row->columnptr[OUT_FEATURE] =
               const_cast<byte*> (itr->first.getValue());
         outHandle->row->lengths[OUT_FEATURE]        =
               itr->first.getByteLength();
         outHandle->row->indicator[OUT_FEATURE]      = true;

         double scoreVal = 0.0;
         if(itr->second.second!=0)
               scoreVal = ((double) itr->second.first) / ((double) itr->second.second);
         outHandle->row->columnptr[OUT_SCORE]         = (byte*) &scoreVal;
         outHandle->row->lengths[OUT_SCORE]           = sizeof(scoreVal);
         outHandle->row->indicator[OUT_SCORE]         = true;

         outHandle->row->columnptr[OUT_PRODUCT_SCORE] = (byte*) &totalScoreAvg;
         outHandle->row->lengths[OUT_PRODUCT_SCORE]   = sizeof(totalScoreAvg);
         outHandle->row->indicator[OUT_PRODUCT_SCORE] = true;

         // writing data to output stream.
         FPF_TblOpWrite(outHandle, NULL);
      }

      if(fearuresList) { FNC_free(fearuresList); fearuresList = NILPTR; }

      // close stream for writing
      FPF_TblOpClose(outHandle, NULL);
      FPF_TblOpClose(icolinfo->Handle, NULL);

   } catch (FpfException &e) {
      if(fearuresList) { FNC_free(fearuresList); fearuresList = NILPTR; }
      errMsg = e.msg;
      FPF_TblOpError("socialvantagescoring:", errMsg.c_str());
   } catch (std::bad_alloc &e) {
      if(fearuresList) { FNC_free(fearuresList); fearuresList = NILPTR; }
      errMsg = "Could not allocate enough memory to complete the request.";
      FPF_TblOpError("socialvantagescoring:", errMsg.c_str());
   } catch (std::exception &e) {
      if(fearuresList) { FNC_free(fearuresList); fearuresList = NILPTR; }
      errMsg = (char *) e.what();
      FPF_TblOpError("socialvantagescoring:", errMsg.c_str());
   } catch (...) {
      if(fearuresList) { FNC_free(fearuresList); fearuresList = NILPTR; }
      errMsg = "Unknown Exception caught.";
      FPF_TblOpError("socialvantagescoring:", errMsg.c_str());
   }

}

