package com.jantarox.woodanalyzer.factory;

import com.jantarox.woodanalyzer.viewmodel.*;

public class ViewModelFactory {

    private ModelFactory modelFactory;

    private ImageViewModel imageViewModel;
    private LoaderViewModel loaderViewModel;
    private GenerateSegmentationViewModel generateSegmentationViewModel;
    private PPIChangerViewModel ppiChangerViewModel;
    private CalculateMeasurementsViewModel calculateMeasurementsViewModel;

    public ViewModelFactory(ModelFactory modelFactory) {
        this.modelFactory = modelFactory;

    }

    public ImageViewModel getImageViewModel() {
        if (imageViewModel == null)
            imageViewModel = new ImageViewModel(modelFactory.getImageModel(), modelFactory.getImageSegmentationModel());
        return imageViewModel;
    }

    public LoaderViewModel getLoaderViewModel() {
        if (loaderViewModel == null)
            loaderViewModel = new LoaderViewModel(modelFactory.getImageModel());

        return loaderViewModel;
    }

    public DirectoryViewModel getDirectoryViewModel() {
        return new DirectoryViewModel(modelFactory.getImageModel());
    }

    public GenerateSegmentationViewModel getGenerateSegmentationViewModel() {
        if (generateSegmentationViewModel == null)
            generateSegmentationViewModel = new GenerateSegmentationViewModel(modelFactory.getImageModel(), modelFactory.getImageSegmentationModel());
        return generateSegmentationViewModel;
    }

    public PPIChangerViewModel getPPIChangerViewModel() {
        if (ppiChangerViewModel == null)
            ppiChangerViewModel = new PPIChangerViewModel(modelFactory.getImageSegmentationModel());

        return ppiChangerViewModel;
    }

    public CalculateMeasurementsViewModel getCalculateMeasurementsViewModel() {
        if (calculateMeasurementsViewModel == null)
            calculateMeasurementsViewModel = new CalculateMeasurementsViewModel(modelFactory.getImageModel(), modelFactory.getImageSegmentationModel());
        return calculateMeasurementsViewModel;
    }
}
