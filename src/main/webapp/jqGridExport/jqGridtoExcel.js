/**
* Created with jquery.
* User: zhangsongl
* Date: 2016-128
* Time: 下午13:30
* To change this template use File | Settings | File Templates.
*/
/**
*
* function jqgridtoExcel (Table,Tal,fileName)
* @param Table 表头table的父div-父div-父div 
* @param Tal jqgrid的tableid
* @param filename Excel文件名称
*   example：jqgirdtpExcel($("#gview_group-report4"),$("#group-report4"),fileName);
* **/
(function(){
    var Base64 = (function() {
        // private property
        var keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

        // private method for UTF-8 encoding
        function utf8Encode(string) {
            string = string.replace(/\r\n/g,"\n");
            var utftext = "";
            for (var n = 0; n < string.length; n++) {
                var c = string.charCodeAt(n);
                if (c < 128) {
                    utftext += String.fromCharCode(c);
                }
                else if((c > 127) && (c < 2048)) {
                    utftext += String.fromCharCode((c >> 6) | 192);
                    utftext += String.fromCharCode((c & 63) | 128);
                }
                else {
                    utftext += String.fromCharCode((c >> 12) | 224);
                    utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                    utftext += String.fromCharCode((c & 63) | 128);
                }
            }
            return utftext;
        }

        // public method for encoding
        return {
            //encode : (typeof btoa == 'function') ? function(input) { return btoa(input); } : function (input) {
            encode : function (input) {
                var output = "";
                var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
                var i = 0;
                input = utf8Encode(input);
                while (i < input.length) {
                    chr1 = input.charCodeAt(i++);
                    chr2 = input.charCodeAt(i++);
                    chr3 = input.charCodeAt(i++);
                    enc1 = chr1 >> 2;
                    enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
                    enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
                    enc4 = chr3 & 63;
                    if (isNaN(chr2)) {
                        enc3 = enc4 = 64;
                    } else if (isNaN(chr3)) {
                        enc4 = 64;
                    }
                    output = output +
                        keyStr.charAt(enc1) + keyStr.charAt(enc2) +
                        keyStr.charAt(enc3) + keyStr.charAt(enc4);
                }
                return output;
            }
        };
    })();
    var format = function(s, c) { return s.replace(/{(\w+)}/g, function(m, p) { return c[p]; }) };
    var tableToExcel = function(table,fileName) {
        var uri = 'data:application/vnd.ms-excel;base64,'
            ,fileName = fileName || 'excelexport'
        , template = '<html xmlns:o="urn:schemas-microsoft-com:office:office"' +
            ' xmlns:x="urn:schemas-microsoft-com:office:exc el" xmlns="http://www.w3.org/TR/REC-html40"><head>' +
            '<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">'+
            '<!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets>' +
            '<x:ExcelWorksheet><x:Name>{worksheet}</x:Name><x:WorksheetOptions><x:DisplayGridlines/>' +
            '</x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml>' +
            '<![endif]--></head><body>{table}</body></html>';

        var ctx = {worksheet:'Worksheet', table: table};
        var a = document.createElement('a');
        document.body.appendChild(a);
        a.hreflang = 'zh';
        a.charset = 'utf8';
        a.type="application/vnd.ms-excel";
        a.href = uri + Base64.encode(format(template,ctx));
        a.target = '_blank';
        a.download = fileName + '.xls';
        a.click();

    };


    window.jqGridtoExcel = function(Table,Tal,fileName) {

        var tableStr = '<table border="1"><thead>{thead}</thead>{tbody}</table>';

        //var thtr = Table.children("div:eq(1)").children("div").children("table").children("thead").children('tr');//获取jqgrid的表头tr
        var thtr = $("#gview_grid-table").children("div.ui-jqgrid-hdiv")
            .children("div.ui-jqgrid-hbox").children("table")
            .children("thead").children('tr');//获取jqgrid的表头tr
        var tbtr = Tal.children("tbody").children('tr');//获取jqgrid内容tr
        var thrlength = thtr.length;//表头tr数
        var tbrlength = tbtr.length;//内容tr数
        var theadStr='';
        var tbodyStr='';

        for(i=0;i<thrlength;i++){//循环获取表头每条tr内容
          if(thtr[i].style.height!='auto'){//获取height不为auto的tr内容
            theadStr +='<tr>';
            //theadStr +=thtr[i].outerHTML;
            var th2 = $("#gview_grid-table").children("div.ui-jqgrid-hdiv")
                .children("div.ui-jqgrid-hbox").children("table")
                .children("thead").children('tr:eq('+i+')').children('th');
            var th2length =th2.length;
            var th2th ='';
            for(y=0;y<th2length;y++){//循环获取tr内display不为none的td，并递加
                //console.log(th2[y].style.display);
                if(th2[y].style.display!='none'){
                   th2th +=th2[y].outerHTML;
                }
            }
            theadStr +=th2th;
            theadStr +='</tr>'
          }
        }

        for(i=1;i<tbrlength;i++){//循环获取内容每条tr内容
            tbodyStr +='<tr>';
            var td2 =Tal.children("tbody").children('tr:eq('+i+')').children('td');
            var td2length = td2.length;
            var td2td = '';
            for(y=0;y<td2length;y++){
                if(td2[y].style.display!='none'){
                   td2td +=td2[y].outerHTML;//输出html
                }
            }
            tbodyStr +=td2td;
            tbodyStr +='</tr>'
        }

//console.log(theadStr);
        tableStr = format(tableStr,{
            thead:theadStr,
            tbody:tbodyStr
        });
        tableToExcel(tableStr,fileName);

    }
})()



