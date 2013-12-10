
from django import forms
from .models import *
from django.core.exceptions import ObjectDoesNotExist

class expenseForm(forms.ModelForm):
	class Meta:
		model = expense
		fields = ('description','cost')

class returnForm(forms.ModelForm):
	class Meta:
		model = retInp
		fields = ('lName', 'fName', 'price', 'feeCollect', 'tYear',
			'tPrep',
			'refLast', 'refFirst',
			'note')

class clientForm(forms.Form):
	lName = forms.CharField(max_length=15)
	fName = forms.CharField(max_length=15)
	refDate = forms.IntegerField()
	tPayID = forms.CharField(widget=forms.HiddenInput())

class whichYear(forms.Form):
    year = forms.ChoiceField(choices=[(x, x) for x in range(2010, 2014)])

class clientReturn(forms.Form):
	clientID = forms.CharField(widget=forms.HiddenInput())
