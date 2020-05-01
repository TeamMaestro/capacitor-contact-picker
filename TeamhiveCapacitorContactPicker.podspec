
  Pod::Spec.new do |s|
    s.name = 'TeamhiveCapacitorContactPicker'
    s.version = '1.0.0'
    s.summary = 'Allows users to select one or many contacts from their device.'
    s.license = 'MIT'
    s.homepage = 'https://github.com/TeamHive/capacitor-contact-picker'
    s.author = 'Sean Perkins'
    s.source = { :git => 'https://github.com/TeamHive/capacitor-contact-picker', :tag => s.version.to_s }
    s.source_files = 'ios/Plugin/**/*.{swift,h,m,c,cc,mm,cpp}'
    s.ios.deployment_target  = '11.0'
    s.dependency 'Capacitor'
  end
