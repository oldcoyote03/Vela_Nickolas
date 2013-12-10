
from django.http import HttpResponse
from django.shortcuts import render_to_response
from django.conf import settings
from django.core.exceptions import ObjectDoesNotExist
from .models import *
from .forms import *
from django.http import HttpResponseRedirect
from django.core.context_processors import csrf
from datetime import date

def home(request):
	Dvals = { 'MEDIA_URL': settings.MEDIA_URL, 'homeImg': 'BC-back.jpg', 'lines': Line.objects.all()}
	return render_to_response("input_tools/home.html", Dvals)

def expenses(request):
	arg = { 'exps': expense.objects.all() }
	return render_to_response("input_tools/expenses.html", arg)

def expenseInput(request):
	if request.POST:
		form = expenseForm(request.POST)
		if form.is_valid():
			form.save()
			arg = { 'exps': expense.objects.all() }
			return render_to_response("input_tools/expenses.html",arg)
	else:
		form = expenseForm()

	args = {}
	args.update(csrf(request))
	args['label'] = 'Expense'
	args['form'] = form
	return render_to_response("input_tools/form_input.html", args)

def retArg(form, request):
	arg = { 'rets': [], 'form': form }
	arg.update(csrf(request))
	if form['year'].value():
		returns = taxReturn.objects.filter(tYear=form['year'].value())
		arg['whichYear'] = form['year'].value()
		for thing in returns:
			arg['rets'].append( (thing,taxPayer.objects.get(id=thing.tPayer_id) ) )
	else:
		returns = taxReturn.objects.filter(tYear=date.today().year)
		arg['whichYear'] = date.today().year
		for thing in returns:
			arg['rets'].append( (thing,taxPayer.objects.get(id=thing.tPayer_id) ) )
	arg['label'] = 'By Year'
	return arg

def returns(request):
	if request.POST:
		form = whichYear(request.POST)
		if form.is_valid():
			arg = retArg(form,request)
		else:
			arg = {}; arg.update(csrf(request))
	else:
		form = whichYear()
		arg = retArg(form,request)
	return render_to_response("input_tools/returns.html", arg)

def TPfunc(formTemp):
	try:
		TP = taxPayer.objects.get(lName=formTemp.lName,fName=formTemp.fName)
	except ObjectDoesNotExist:
		TP = taxPayer(lName = formTemp.lName, fName = formTemp.fName)

	if formTemp.refLast:
		try:
			TP.refBy = taxPayer.objects.get(lName=formTemp.refLast,fName=formTemp.refFirst)
			TP.refDate = date.today().year
		except ObjectDoesNotExist: None
	
	return TP
	
def TRfunc(formTemp,tPay):
	newReturn = taxReturn()
	newReturn.tPayer = tPay
	newReturn.fileDate = formTemp.fileDate
	newReturn.price = formTemp.price
	newReturn.feeCollect = formTemp.feeCollect
	newReturn.tYear = formTemp.tYear
	newReturn.tPrep = formTemp.tPrep
	newReturn.note = formTemp.note
	return newReturn

def returnInput(request):
	if request.POST:
		form = returnForm(request.POST)
		if form.is_valid():
			
			formTemp = form.save()
			tPay = TPfunc(formTemp)
			tPay.save()

			newReturn = TRfunc(formTemp,tPay)
			newReturn.save()
			
			formYear = whichYear()
			arg = retArg(formYear, request)
			return render_to_response("input_tools/retInputLanding.html", arg)
	else:
		form = returnForm()

	args = {}
	args.update(csrf(request))
	args['label'] = 'Return'
	args['form'] = form
	return render_to_response("input_tools/form_input.html", args)

def cliArg(request):
	arg = {}; arg.update(csrf(request))
	arg['clis'] = []
	for thing in taxPayer.objects.all().order_by('lName'):
		fid = thing.id
		try:
			refTemp = taxPayer.objects.get(id=thing.refBy_id)
			form_fields = { 'lName': refTemp.lName,
				'fName': refTemp.fName, 'refDate': thing.refDate }
			form = clientForm(form_fields)
			arg['clis'].append( (thing,form,fid) )

		except ObjectDoesNotExist:
			form = clientForm()
			arg['clis'].append( (thing,form,fid) )
	return arg

def clients(request):

	if request.POST:
		form = clientForm(request.POST)
		formR = clientReturn(request.POST)
		if form.is_valid():
			try:
				referrer = taxPayer.objects.get(id=form['tPayID'].value())
				referral = taxPayer.objects.get(
					lName=form['lName'].value(),fName=form['fName'].value())

				referrer.refBy = referral
				referrer.refDate = form['refDate'].value()
				referrer.save()

				arg = cliArg(request)
				return render_to_response("input_tools/client_list.html", arg)
			
			except ObjectDoesNotExist: None

		elif formR.is_valid():
			try:
				arg = {
					'firstName': taxPayer.objects.get(id=formR['clientID'].value()).fName,
					'lastName': taxPayer.objects.get(id=formR['clientID'].value()).lName,
					'rets': taxReturn.objects.filter(tPayer=formR['clientID'].value())
				}
				arg.update(csrf(request))
				return render_to_response("input_tools/clientreturns.html",arg)
			except ObjectDoesNotExist:
				None
		
	arg= cliArg(request)
	return render_to_response("input_tools/client_list.html", arg)
