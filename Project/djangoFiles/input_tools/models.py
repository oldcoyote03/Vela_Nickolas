
from django.db import models
from datetime import date

class taxPayer(models.Model):
	fName = models.CharField(max_length=15)
	lName = models.CharField(max_length=15)
	refBy = models.ForeignKey('self', null=True)
	refDate = models.IntegerField(null=True)

class taxReturn(models.Model):
	tPayer = models.ForeignKey('taxPayer', null=True)
	fileDate = models.DateField()
	price = models.IntegerField()
	feeCollect = models.BooleanField()
	tYear = models.IntegerField()
	tPrep = models.CharField(max_length=2)
	note = models.CharField(max_length=40)

class retInp(models.Model):
	lName = models.CharField(max_length=15)
	fName = models.CharField(max_length=15)
	fileDate = models.DateField(default=date.today)
	price = models.IntegerField()
	feeCollect = models.BooleanField(default=False)
	tYear = models.IntegerField()
	tPrep = models.CharField(max_length=2)
	refLast = models.CharField(max_length=15, blank=True)
	refFirst = models.CharField(max_length=15, blank=True)
	note = models.CharField(max_length=40)

class expense(models.Model):
	cost = models.IntegerField()
	eDate = models.DateField(default=date.today)
	description = models.CharField(max_length=40)

class Line(models.Model):
	text = models.CharField(max_length=255)
