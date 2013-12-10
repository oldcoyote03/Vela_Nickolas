
from django.conf.urls import patterns, include, url
from django.conf import settings

# Uncomment the next two lines to enable the admin:
from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
	url(r'^$', 'input_tools.views.home', name='home'),

    url(r'^expenses/$', 'input_tools.views.expenses', name='expenses'),
    url(r'^expenseinput/$', 'input_tools.views.expenseInput', name='expenseinput'),

    url(r'^returns/$', 'input_tools.views.returns', name='returns'),
    url(r'^returninput/$', 'input_tools.views.returnInput', name='returninput'),

    url(r'^clients/$', 'input_tools.views.clients', name='clients'),

    url(r'^media/(?P<path>.*)$', 'django.views.static.serve',
        {'document_root': settings.MEDIA_ROOT }),

    # Examples:
    # url(r'^$', 'djangoFiles.views.home', name='home'),
    # url(r'^djangoFiles/', include('djangoFiles.foo.urls')),

    # Uncomment the admin/doc line below to enable admin documentation:
    # url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    url(r'^admin/', include(admin.site.urls)),
)
