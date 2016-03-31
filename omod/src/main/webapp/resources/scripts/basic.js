/*
 * SimpleModal Basic Modal Dialog
 * http://www.ericmmartin.com/projects/simplemodal/
 * http://code.google.com/p/simplemodal/
 *
 * Copyright (c) 2010 Eric Martin - http://ericmmartin.com
 *
 * Licensed under the MIT license:
 *   http://www.opensource.org/licenses/mit-license.php
 *
 * Revision: $Id: basic.js 243 2010-03-15 14:23:14Z emartin24 $
 *
 */

jQuery(function ($) {
	$('#print_lab_ordonance').click(function (e) {
		$("#laborder-modal-content").modal();
		$("#simplemodal-container").css({'width':'1100px', 'min-height':'600px', 'left':'300px', 'right':'300px', 'top':'50px'});
		  return false;
	});
});