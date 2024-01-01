<#macro subscriptionForm productSlug subscriptionFormMapping formClass>
  <style type="text/css" nonce="${_cspNonce}">
     100% {
     transform: rotate(360deg);
     }
     }
     .ml-form-embedContainer {
     box-sizing: border-box;
     display: table;
     margin: 0 auto;
     position: static;
     width: 100% !important;
     color: black;
     }
     .ml-form-embedContainer h4,
     .ml-form-embedContainer p,
     .ml-form-embedContainer span,
     .ml-form-embedContainer button {
     text-transform: none !important;
     letter-spacing: normal !important;
     }
     .ml-form-embedContainer .ml-form-embedWrapper {
     border-width: 0px;
     border-color: transparent;
     border-radius: 4px;
     border-style: solid;
     box-sizing: border-box;
     display: inline-block !important;
     margin: 0;
     padding: 0;
     position: relative;
     }
     .ml-form-embedContainer .ml-form-embedWrapper.embedPopup,
     .ml-form-embedContainer .ml-form-embedWrapper.embedForm { width: 100%; }
     .ml-form-embedContainer .ml-form-align-left { text-align: left; }
     .ml-form-embedContainer .ml-form-align-center { text-align: center; }
     .ml-form-embedContainer .ml-form-align-default { display: table-cell !important; vertical-align: middle !important; text-align: center !important; }
     .ml-form-embedContainer .ml-form-align-right { text-align: right; }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedHeader img {
     border-top-left-radius: 4px;
     border-top-right-radius: 4px;
     height: auto;
     margin: 0 auto !important;
     max-width: 100%;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody,
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-successBody {
     padding: 20px 20px 0 20px;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody.ml-form-embedBodyHorizontal {
     padding-bottom: 0;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedContent,
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-successBody .ml-form-successContent {
     color: black;
     text-align: left;
     margin: 0 0 20px 0;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedContent h4,
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-successBody .ml-form-successContent h4 {
     color: #d2691e;
     font-family: 'Open Sans', Arial, Helvetica, sans-serif;
     font-size: 20px;
     font-weight: 700;
     margin: 0 0 10px 0;
     text-align: left;
     word-break: break-word;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedContent p,
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-successBody .ml-form-successContent p {
     color: #000000;
     font-family: 'Open Sans', Arial, Helvetica, sans-serif;
     font-size: 14px;
     font-weight: 400;
     line-height: 20px;
     margin: 0 0 10px 0;
     text-align: left;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedContent ul,
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedContent ol,
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-successBody .ml-form-successContent ul,
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-successBody .ml-form-successContent ol {
     color: #000000;
     font-family: 'Open Sans', Arial, Helvetica, sans-serif;
     font-size: 14px;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedContent ol ol,
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-successBody .ml-form-successContent ol ol {
     list-style-type: lower-alpha;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedContent ol ol ol,
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-successBody .ml-form-successContent ol ol ol {
     list-style-type: lower-roman;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedContent p a,
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-successBody .ml-form-successContent p a {
     color: #000000;
     text-decoration: underline;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .subscription-form .ml-field-group {
     text-align: left!important;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .subscription-form .ml-field-group label {
     margin-bottom: 5px;
     color: #333333;
     font-size: 14px;
     font-family: 'Open Sans', Arial, Helvetica, sans-serif;
     font-style: normal; text-decoration: none;;
     display: inline-block;
     line-height: 20px;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedContent p:last-child,
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-successBody .ml-form-successContent p:last-child {
     margin: 0;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody form {
     margin: 0;
     width: 100%;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-formContent,
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow {
     margin: 0 0 20px 0;
     width: 100%;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow {
     float: left;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-formContent.horozintalForm {
     margin: 0;
     padding: 0 0 20px 0;
     width: 100%;
     height: auto;
     float: left;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow {
     margin: 0 0 10px 0;
     width: 100%;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow.ml-last-item {
     margin: 0;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow.ml-formfieldHorizintal {
     margin: 0;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow input {
     color: #333333 !important;
     border-color: #cccccc;
     border-radius: 4px !important;
     border-style: solid !important;
     border-width: 1px !important;
     font-family: 'Open Sans', Arial, Helvetica, sans-serif;
     font-size: 14px !important;
     height: auto;
     line-height: 21px !important;
     margin-bottom: 0;
     margin-top: 0;
     margin-left: 0;
     margin-right: 0;
     padding: 10px 10px !important;
     width: 100% !important;
     box-sizing: border-box !important;
     max-width: 100% !important;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow input::-webkit-input-placeholder,
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow input::-webkit-input-placeholder { color: #333333; }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow input::-moz-placeholder,
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow input::-moz-placeholder { color: #333333; }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow input:-ms-input-placeholder,
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow input:-ms-input-placeholder { color: #333333; }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow input:-moz-placeholder,
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow input:-moz-placeholder { color: #333333; }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow textarea, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow textarea {
     color: #333333 !important;
     border-color: #cccccc;
     border-radius: 4px !important;
     border-style: solid !important;
     border-width: 1px !important;
     font-family: 'Open Sans', Arial, Helvetica, sans-serif;
     font-size: 14px !important;
     height: auto;
     line-height: 21px !important;
     margin-bottom: 0;
     margin-top: 0;
     padding: 10px 10px !important;
     width: 100% !important;
     box-sizing: border-box !important;
     max-width: 100% !important;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-radio .custom-control-label::before, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-radio .custom-control-label::before, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-checkbox .custom-control-label::before, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-checkbox .custom-control-label::before, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox .label-description::before, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-interestGroupsRow .ml-form-interestGroupsRowCheckbox .label-description::before, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow .label-description::before {
     border-color: #cccccc!important;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow input.custom-control-input[type="checkbox"]{
     box-sizing: border-box;
     padding: 0;
     position: absolute;
     z-index: -1;
     opacity: 0;
     margin-top: 5px;
     margin-left: -1.5rem;
     overflow: visible;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-checkbox .custom-control-label::before, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-checkbox .custom-control-label::before, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox .label-description::before, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-interestGroupsRow .ml-form-interestGroupsRowCheckbox .label-description::before, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow .label-description::before {
     border-radius: 4px!important;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow input[type=checkbox]:checked~.label-description::after, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox input[type=checkbox]:checked~.label-description::after, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-checkbox .custom-control-input:checked~.custom-control-label::after, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-checkbox .custom-control-input:checked~.custom-control-label::after, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-interestGroupsRow .ml-form-interestGroupsRowCheckbox input[type=checkbox]:checked~.label-description::after {
     background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 8 8'%3e%3cpath fill='%23fff' d='M6.564.75l-3.59 3.612-1.538-1.55L0 4.26 2.974 7.25 8 2.193z'/%3e%3c/svg%3e");
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-radio .custom-control-input:checked~.custom-control-label::after, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-radio .custom-control-input:checked~.custom-control-label::after {
     background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='-4 -4 8 8'%3e%3ccircle r='3' fill='%23fff'/%3e%3c/svg%3e");
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-radio .custom-control-input:checked~.custom-control-label::before, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-radio .custom-control-input:checked~.custom-control-label::before, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-checkbox .custom-control-input:checked~.custom-control-label::before, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-checkbox .custom-control-input:checked~.custom-control-label::before, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox input[type=checkbox]:checked~.label-description::before, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-interestGroupsRow .ml-form-interestGroupsRowCheckbox input[type=checkbox]:checked~.label-description::before, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow input[type=checkbox]:checked~.label-description::before  {
     border-color: #000000!important;
     background-color: #000000!important;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-radio .custom-control-label::before, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-radio .custom-control-label::before, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-radio .custom-control-label::after, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-radio .custom-control-label::after, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-checkbox .custom-control-label::before, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-checkbox .custom-control-label::after, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-checkbox .custom-control-label::before, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-checkbox .custom-control-label::after {
     top: 2px;
     box-sizing: border-box;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox .label-description::before, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox .label-description::after, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow .label-description::before, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow .label-description::after {
     top: 0px!important;
     box-sizing: border-box!important;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow .label-description::before, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow .label-description::after {
     top: 0px!important;
     box-sizing: border-box!important;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-interestGroupsRow .ml-form-interestGroupsRowCheckbox .label-description::after {
     top: 0px!important;
     box-sizing: border-box!important;
     position: absolute;
     left: -1.5rem;
     display: block;
     width: 1rem;
     height: 1rem;
     content: "";
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-interestGroupsRow .ml-form-interestGroupsRowCheckbox .label-description::before {
     top: 0px!important;
     box-sizing: border-box!important;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .custom-control-label::before {
     position: absolute;
     top: 4px;
     left: -1.5rem;
     display: block;
     width: 16px;
     height: 16px;
     pointer-events: none;
     content: "";
     border: #adb5bd solid 1px;
     border-radius: 50%;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .custom-control-label::after {
     position: absolute;
     top: 2px!important;
     left: -1.5rem;
     display: block;
     width: 1rem;
     height: 1rem;
     content: "";
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox .label-description::before, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-interestGroupsRow .ml-form-interestGroupsRowCheckbox .label-description::before, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow .label-description::before {
     position: absolute;
     top: 4px;
     left: -1.5rem;
     display: block;
     width: 16px;
     height: 16px;
     pointer-events: none;
     content: "";
     border: #adb5bd solid 1px;
     border-radius: 50%;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox .label-description::after {
     position: absolute;
     top: 0px!important;
     left: -1.5rem;
     display: block;
     width: 1rem;
     height: 1rem;
     content: "";
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow .label-description::after {
     position: absolute;
     top: 0px!important;
     left: -1.5rem;
     display: block;
     width: 1rem;
     height: 1rem;
     content: "";
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .custom-radio .custom-control-label::after {
     background: no-repeat 50%/50% 50%;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .custom-checkbox .custom-control-label::after, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox .label-description::after, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-interestGroupsRow .ml-form-interestGroupsRowCheckbox .label-description::after, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow .label-description::after {
     background: no-repeat 50%/50% 50%;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-control, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-control {
     position: relative;
     display: block;
     min-height: 1.5rem;
     padding-left: 1.5rem;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-radio .custom-control-input, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-radio .custom-control-input, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-checkbox .custom-control-input, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-checkbox .custom-control-input {
     position: absolute;
     z-index: -1;
     opacity: 0;
     box-sizing: border-box;
     padding: 0;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-radio .custom-control-label, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-radio .custom-control-label, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-checkbox .custom-control-label, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-checkbox .custom-control-label {
     color: #000000;
     font-size: 12px!important;
     font-family: 'Open Sans', Arial, Helvetica, sans-serif;
     line-height: 22px;
     margin-bottom: 0;
     position: relative;
     vertical-align: top;
     font-style: normal;
     font-weight: 700;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-fieldRow .custom-select, .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow .custom-select {
     color: #333333 !important;
     border-color: #cccccc;
     border-radius: 4px !important;
     border-style: solid !important;
     border-width: 1px !important;
     font-family: 'Open Sans', Arial, Helvetica, sans-serif;
     font-size: 14px !important;
     line-height: 20px !important;
     margin-bottom: 0;
     margin-top: 0;
     padding: 10px 28px 10px 12px !important;
     width: 100% !important;
     box-sizing: border-box !important;
     max-width: 100% !important;
     height: auto;
     display: inline-block;
     vertical-align: middle;
     -webkit-appearance: none;
     -moz-appearance: none;
     appearance: none;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow {
     height: auto;
     width: 100%;
     float: left;
     }
     .ml-form-formContent.horozintalForm .ml-form-horizontalRow .ml-input-horizontal { width: 70%; float: left; }
     .ml-form-formContent.horozintalForm .ml-form-horizontalRow .ml-button-horizontal { width: 30%; float: left; }
     .ml-form-formContent.horozintalForm .ml-form-horizontalRow .ml-button-horizontal.labelsOn { padding-top: 25px;  }
     .ml-form-formContent.horozintalForm .ml-form-horizontalRow .horizontal-fields { box-sizing: border-box; float: left; padding-right: 10px;  }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow input {
     color: #333333;
     border-color: #cccccc;
     border-radius: 4px;
     border-style: solid;
     border-width: 1px;
     font-family: 'Open Sans', Arial, Helvetica, sans-serif;
     font-size: 14px;
     line-height: 20px;
     margin-bottom: 0;
     margin-top: 0;
     padding: 10px 10px;
     width: 100%;
     box-sizing: border-box;
     overflow-y: initial;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow button {
     background-color: #d2691e !important;
     border-color: #d2691e;
     border-style: solid;
     border-width: 1px;
     border-radius: 4px;
     box-shadow: none;
     color: #ffffff !important;
     cursor: pointer;
     font-family: 'Open Sans', Arial, Helvetica, sans-serif;
     font-size: 14px !important;
     font-weight: 700;
     line-height: 20px;
     margin: 0 !important;
     padding: 10px !important;
     width: 100%;
     height: auto;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-horizontalRow button:hover {
     background-color: #ff6a00 !important;
     border-color: #ff6a00 !important;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow input[type="checkbox"] {
     box-sizing: border-box;
     padding: 0;
     position: absolute;
     z-index: -1;
     opacity: 0;
     margin-top: 5px;
     margin-left: -1.5rem;
     overflow: visible;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow .label-description {
     color: #000000;
     display: block;
     font-family: 'Open Sans', Arial, Helvetica, sans-serif;
     font-size: 12px;
     text-align: left;
     margin-bottom: 0;
     position: relative;
     vertical-align: top;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow label {
     font-weight: normal;
     margin: 0;
     padding: 0;
     position: relative;
     display: block;
     min-height: 24px;
     padding-left: 24px;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow label a {
     color: #000000;
     text-decoration: underline;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow label p {
     color: #000000 !important;
     font-family: 'Open Sans', Arial, Helvetica, sans-serif !important;
     font-size: 12px !important;
     font-weight: normal !important;
     line-height: 18px !important;
     padding: 0 !important;
     margin: 0 5px 0 0 !important;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow label p:last-child {
     margin: 0;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedSubmit {
     margin: 0 0 20px 0;
     float: left;
     width: 100%;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedSubmit button {
     background-color: #d2691e !important;
     border: none !important;
     border-radius: 4px !important;
     box-shadow: none !important;
     color: #ffffff !important;
     cursor: pointer;
     font-family: 'Open Sans', Arial, Helvetica, sans-serif !important;
     font-size: 14px !important;
     font-weight: 700 !important;
     line-height: 21px !important;
     height: auto;
     padding: 10px !important;
     width: 100% !important;
     box-sizing: border-box !important;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedSubmit button:hover {
     background-color: #ff6a00 !important;
     }
     .ml-subscribe-close {
     width: 30px;
     height: 30px;
     background-size: 30px;
     cursor: pointer;
     margin-top: -10px;
     margin-right: -10px;
     position: absolute;
     top: 0;
     right: 0;
     }
     .ml-error input, .ml-error textarea, .ml-error select {
     border-color: red!important;
     }
     .ml-error .custom-checkbox-radio-list {
     border: 1px solid red !important;
     border-radius: 4px;
     padding: 10px;
     }
     .ml-error .label-description,
     .ml-error .label-description p,
     .ml-error .label-description p a,
     .ml-error label:first-child {
     color: #ff0000 !important;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow.ml-error .label-description p,
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-checkboxRow.ml-error .label-description p:first-letter {
     color: #ff0000 !important;
     }
     @media only screen {
     .ml-form-embedWrapper.embedDefault, .ml-form-embedWrapper.embedPopup { width: 100%!important; }
     .ml-form-formContent.horozintalForm { float: left!important; }
     .ml-form-formContent.horozintalForm .ml-form-horizontalRow { height: auto!important; width: 100%!important; float: left!important; }
     .ml-form-formContent.horozintalForm .ml-form-horizontalRow .ml-input-horizontal { width: 100%!important; }
     .ml-form-formContent.horozintalForm .ml-form-horizontalRow .ml-input-horizontal > div { padding-right: 0px!important; padding-bottom: 10px; }
     .ml-form-formContent.horozintalForm .ml-button-horizontal { width: 100%!important; }
     .ml-form-formContent.horozintalForm .ml-button-horizontal.labelsOn { padding-top: 0px!important; }
     }

     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions { text-align: left; float: left; width: 100%; }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsContent {
     margin: 0 0 15px 0;
     text-align: left;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsContent.horizontal {
     margin: 0 0 15px 0;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsContent h4 {
     color: #000000;
     font-family: 'Open Sans', Arial, Helvetica, sans-serif;
     font-size: 12px;
     font-weight: 700;
     line-height: 18px;
     margin: 0 0 10px 0;
     word-break: break-word;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsContent p {
     color: #000000;
     font-family: 'Open Sans', Arial, Helvetica, sans-serif;
     font-size: 12px;
     line-height: 18px;
     margin: 0 0 10px 0;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsContent.privacy-policy p {
     color: #000000;
     font-family: 'Open Sans', Arial, Helvetica, sans-serif;
     font-size: 12px;
     line-height: 22px;
     margin: 0 0 10px 0;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsContent.privacy-policy p a {
     color: #000000;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsContent.privacy-policy p:last-child {
     margin: 0;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsContent p a {
     color: #000000;
     text-decoration: underline;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsContent p:last-child { margin: 0 0 15px 0; }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptions {
     margin: 0;
     padding: 0;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox {
     margin: 0 0 10px 0;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox:last-child {
     margin: 0;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox label {
     font-weight: normal;
     margin: 0;
     padding: 0;
     position: relative;
     display: block;
     min-height: 24px;
     padding-left: 24px;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox .label-description {
     color: #000000;
     font-family: 'Open Sans', Arial, Helvetica, sans-serif;
     font-size: 12px;
     line-height: 18px;
     text-align: left;
     margin-bottom: 0;
     position: relative;
     vertical-align: top;
     font-style: normal;
     font-weight: 700;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox .description {
     color: #000000;
     font-family: 'Open Sans', Arial, Helvetica, sans-serif;
     font-size: 12px;
     font-style: italic;
     font-weight: 400;
     line-height: 18px;
     margin: 5px 0 0 0;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsOptionsCheckbox input[type="checkbox"] {
     box-sizing: border-box;
     padding: 0;
     position: absolute;
     z-index: -1;
     opacity: 0;
     margin-top: 5px;
     margin-left: -1.5rem;
     overflow: visible;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedMailerLite-GDPR {
     padding-bottom: 20px;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedMailerLite-GDPR p {
     color: #000000;
     font-family: 'Open Sans', Arial, Helvetica, sans-serif;
     font-size: 10px;
     line-height: 14px;
     margin: 0;
     padding: 0;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedMailerLite-GDPR p a {
     color: #000000;
     text-decoration: underline;
     }
     @media (max-width: 768px) {
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedPermissionsContent p {
     font-size: 12px !important;
     line-height: 18px !important;
     }
     .ml-form-embedContainer .ml-form-embedWrapper .ml-form-embedBody .ml-form-embedPermissions .ml-form-embedMailerLite-GDPR p {
     font-size: 10px !important;
     line-height: 14px !important;
     }
     }
  </style>
  <div class="ml-form-embedContainer ml-subscribe-form ml-subscribe-form-8308481">
    <!-- NOTE: ml-form-align-left instead of ml-form-align-center (like social networks box) -->
     <div class="ml-form-align-left">
        <div class="ml-form-embedWrapper embedForm">
           <div class="ml-form-embedBody ml-form-embedBodyDefault row-form">
              <div class="ml-form-embedContent">
                 <h4>${xlat['mailer-lite.form.title']}</h4>
                 <#if xlat['mailer-lite.form.text']??>${xlat['mailer-lite.form.text']}</#if>
              </div>
              <#assign fields = subscriptionFormMapping.fields>
              <form class="subscription-form ${formClass}" action="/produkt/${productSlug}/subscribe" data-code="" method="post" target="_blank">
                 <div class="ml-form-formContent">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <input type="hidden" name="${fields.antispamQuestion.name}" value="${fields.antispamQuestion.value!}"/>
                    <div class="ml-form-fieldRow">
                       <div class="ml-field-group ml-field-name">
                          <input type="text" class="form-control" name="${fields.name.name}" value="${fields.name.value!}" aria-label="name" data-inputmask="" placeholder="Jméno" autocomplete="given-name">
                       </div>
                    </div>
                    <div class="ml-form-fieldRow">
                       <div class="ml-field-group ml-field-email ml-validate-email ml-validate-required">
                          <input type="email" class="form-control" name="${fields.email.name}" value="${fields.email.value!}" aria-label="email" aria-required="true" required data-inputmask="" placeholder="Email" autocomplete="email">
                       </div>
                    </div>
                    <div class="ml-form-fieldRow ml-last-item">
                       <div class="ml-field-group ml-validate-required">
                          <label class="col-form-label cursor-help  ml-form-embedContent" title="Kontrolní otázka - ochrana proti robotům">${fields.antispamQuestion.value!}</label><br/>
                          <input type="text" class="form-control" name="${fields.antispamAnswer.name}" value="${fields.antispamAnswer.value!}" required data-inputmask=""/>
                       </div>
                    </div>
                 </div>
                 <div class="ml-form-embedPermissions">
                    <div class="ml-form-embedPermissionsContent default">
                       <p><em>Vaše osobní údaje (jméno, e-mailová adresa) jsou u mě v bezpečí a budu je na základě vašeho souhlasu zpracovávat podle&nbsp;<a href="${xlat['personal-data.protection.url']}" target="_blank">zásad ochrany osobních údajů</a>, které vycházejí z české a evropské legislativy.</em></p>
                       <p><em>Stisknutím tlačítka vyjadřujete svůj souhlas s tímto zpracováním potřebným pro zaslání e-booku a dalších newsletterů ode mě, které se budou týkat souvisejícího tématu.</em></p>
                       <p><em>Svůj souhlas můžete kdykoli odvolat kliknutím na tlačítko ODHLÁSIT v každém zaslaném e-mailu.</em></p>
                       <div class="ml-form-embedPermissionsOptions">
                       </div>
                    </div>
                 </div>
                 <div class="ml-form-embedSubmit">
                    <button type="submit" class="primary">ODESLAT</button>
                 </div>
              </form>
           </div>
        </div>
     </div>
  </div>
</#macro>
