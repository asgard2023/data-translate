//定义支持国际化的语言
var transTypeDist = 'en,tw,ja';
var isLoadTransType = false;

var transFields;
var isLoadFields = false;


(function () {
    if (typeof jQuery != 'undefined') {
        trans_getTypeDists();
    }
})();

function getQueryString (name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null)
        return unescape(r[2]);
    return null;
}

/**
 * 支持的翻译语言
 * @returns {*}
 */
function trans_getTypeDists() {
    if (!isLoadTransType) {
        isLoadTransType = true;
        $.ajax({
            url: '/dflsystem/trTransType/typeDists',
            type: 'GET',
            async: false,
            cache: false,
            success: function (res) {
                transTypeDist = res.data;
                console.log('----support trans dist types=' + transTypeDist);
            },
            error: function (returndata) {
                alert(JSON.stringify(returndata));
            }
        });
    }
}

/**
 * 支持翻译的属性
 * @returns {*}
 */
function trans_getTransFields(className) {
    if (!isLoadFields) {
        isLoadFields = true;
        $.ajax({
            url: '/dflsystem/trTransType/transFields?className=' + className,
            type: 'GET',
            async: false,
            cache: false,
            success: function (res) {
                if (res.data) {
                    transFields = res.data;
                    console.log('----support className='+className+' transFields=' + transFields);
                }
            },
            error: function (returndata) {
                alert(JSON.stringify(returndata));
            }
        });
    }
}

function uppercaseFirst(str) {
    return str.slice(0, 1).toUpperCase() + str.slice(1).toLowerCase();
}

/**
 * 用于数据load成功后，页面数据渲染前
 * @param rows
 * @param transTypeCode
 */
function trans_loadSuccessIds(rows, transTypeCode, typeInfo) {
    //transFields以url参数为优先
    var transFieldParam=getQueryString('transFields');
    if(!transFieldParam && typeInfo && typeInfo.transFields){
        transFields=typeInfo.transFields.join(',');
    }
    console.log('-----loadSuccessIds--transTypeCode='+transTypeCode);
    rowIds = '';
    var obj;
    for (var i = 0; i < rows.length; i++) {
        obj = rows[i];
        rowIds += obj.id + ',';
    }
    langDicts = trans_getFieldDictsByDataId(transTypeCode, transTypeDist, rowIds, null);
    for (var i = 0; i < rows.length; i++) {
        obj = rows[i];
        trans_extDataFields(obj, transFields);
    }
}

function trans_loadSuccessId(obj, transTypeCode) {
    console.log('-----loadSuccessId--transTypeCode='+transTypeCode);
    rowIds = '' + obj.id;
    langDicts = trans_getFieldDictsByDataId(transTypeCode, transTypeDist, rowIds, null);
    trans_extDataFields(obj, transFields);
}

function trans_extDataFields(obj, transFields) {
    var langs = transTypeDist.split(',');
    var fields = transFields.split(',');
    for (var i = 0; i < langs.length; i++) {
        var lang = langs[i];
        for (var j = 0; j < fields.length; j++) {
            var field = fields[j];
            var fieldName = field + uppercaseFirst(lang);
            var fieldValue = trans_getLangItemContent(field, langDicts, obj.id, null, lang);
            obj[fieldName] = fieldValue;
            //console.log('fieldName=' + fieldName + ' fieldValue=' + fieldValue + ' ' + obj[fieldName]);
        }
    }
}

/**
 * 用于jqGrid的loadComplete
 * @param jqId
 * @param rows
 * @param transTypeCode
 */
function trans_jqGridCells(jqId, rows, transTypeCode, typeInfo) {
    //transFields以url参数为优先
    var transFieldParam=getQueryString('transFields');
    if(!transFieldParam && typeInfo && typeInfo.transFields){
        transFields=typeInfo.transFields.join(',');
    }
    console.log('-----jqGridCells--transTypeDist=' + transTypeDist + ' transTypeCode=' + transTypeCode + ' transFields=' + transFields);
    rowIds = '';
    var obj;
    for (var i = 0; i < rows.length; i++) {
        obj = rows[i];
        rowIds += obj.id + ',';
    }
    langDicts = trans_getFieldDictsByDataId(transTypeCode, transTypeDist, rowIds, null);
    for (var i = 0; i < rows.length; i++) {
        obj = rows[i];
        trans_jqGridSetCell(jqId, obj, transFields);
    }
}

function trans_jqGridSetCell(jqId, obj, transFields) {
    var langs = transTypeDist.split(',');
    var fields = transFields.split(',');
    for (var i = 0; i < langs.length; i++) {
        var lang = langs[i];
        for (var j = 0; j < fields.length; j++) {
            var field = fields[j];
            var fieldName = field + uppercaseFirst(lang);
            var fieldValue = trans_getLangItemContent(field, langDicts, obj.id, null, lang);
            obj[fieldName] = fieldValue;
            $('#' + jqId).jqGrid('setCell', obj.id, fieldName, fieldValue);
            //console.log('--trans_jqGridSetCell--fieldName=' + fieldName + ' fieldValue=' + fieldValue + ' ' + obj[fieldName]);
        }
    }
}


function trans_saveLocalLanguage(id, postdata, transFields) {
    if (!id) {
        console.warn('仅支持数据修改，即id不为空');
        return;
    }
    var langs = transTypeDist.split(',');
    var fields = transFields.split(',');
    for (var i = 0; i < langs.length; i++) {
        var lang = langs[i];
        for (var j = 0; j < fields.length; j++) {
            var field = fields[j];
            trans_saveLangItemContent(transTypeCode, field, id, null, lang, postdata[field + uppercaseFirst(lang)]);
        }
    }
}

function trans_getFieldDictsByDataId(transTypeCode, transTypeDist, dataNids, dataSids) {
    var dataObj;
    var codes = '';
    var paramObj = {
        pageSize: 1000,
        pageNum: 1,
        transTypeCode: transTypeCode,
        transTypeDist: transTypeDist
    }
    if (dataNids) {
        for (var i = 0; i < dataNids.length; i++) {
            codes += dataNids[i] + ',';
        }
        paramObj.dataNids = dataNids;
    }
    if (dataSids) {
        for (var i = 0; i < dataSids.length; i++) {
            codes += dataSids[i] + ',';
        }
        paramObj.dataSids = dataSids;
    }


    $.ajax({
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        url: '/dflsystem/trTransData/listDict',
        type: 'GET',
        dataType: 'json',
        data: paramObj,
        async: false,
        cache: false,
        success: function (res) {
            dataObj = res.list;
            return dataObj;
        },
        error: function (returndata) {
            alert(JSON.stringify(returndata));
        }
    });
    return dataObj;
}

function trans_getLangItemContent(field, langDicts, dataNid, dataSid, lang) {
    if (!langDicts) {
        console.log('---field=' + field + ' langDicts=' + langDicts);
        return '';
    }
    // console.log('---field=' + field + ' dataNid=' + dataNid + ' lang=' + lang + ' langDicts=' + langDicts);
    for (var i = 0; i < langDicts.length; i++) {
        var dic = langDicts[i];
        if (dic.code == field && dic.lang == lang) {
            if (dataNid && dic.dataNid == dataNid) {
                return dic.content;
            }
            if (dataSid && dic.dataSid == dataSid) {
                return dic.content;
            }
        }
    }
    return '';
}

function trans_saveLangItemContent(transTypeCode, field, dataNid, dataSid, lang, content) {
    if (!content) {
        console.log('field=' + field + ' dataNid=' + dataNid + ' lang=' + lang + ' content=' + content);
        return;
    }
    var dataObj = {
        transTypeCode: transTypeCode,
        code: field,
        lang: lang,
        content: content
    };
    if (dataSid) {
        dataObj.dataSid = dataSid;
    }
    if (dataNid) {
        dataObj.dataNid = dataNid;
    }

    $.ajax({
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        url: '/dflsystem/trTransData/saveJson',
        type: 'POST',
        data: JSON.stringify(dataObj),
        async: false,
        cache: false,
        success: function (res) {
            //data = res.data;
        },
        error: function (returndata) {
            alert(JSON.stringify(returndata));
        }
    });
}